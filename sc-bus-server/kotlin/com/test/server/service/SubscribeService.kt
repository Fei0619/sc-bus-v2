package com.test.server.service

import com.mongodb.DuplicateKeyException
import com.test.common.constant.PushType
import com.test.common.exception.AlterException
import com.test.common.result.Res
import com.test.server.context.AutoSubscribeContext
import com.test.server.dto.AutoSubscribeDto
import com.test.server.mongo.document.ServiceDetails
import com.test.server.mongo.document.Subscription
import com.test.server.mongo.repository.ServiceDetailsRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

/**
 * @author 费世程
 * @date 2020/8/2 17:43
 */
@Service
class SubscribeService(private val serviceDetailsRepository: ServiceDetailsRepository,
                       private val cacheService: CacheService) {

  private val log = LoggerFactory.getLogger(SubscribeService::class.java)

  /**
   * 自动订阅
   * @param dto AutoSubscribeDto - 自动订阅信息
   * @return
   * @author 费世程
   * @date 2020/8/10 0:28
   */
  fun autoSubscribe(dto: AutoSubscribeDto): Mono<Res<ServiceDetails>> {
    //1.校验参数
    val serviceId = dto.serviceId
    if (serviceId == null || StringUtils.isEmpty(serviceId)) {
      return Mono.just(Res.error("serviceId不能为空！"))
    }
    val serviceCode = dto.serviceCode
    if (serviceCode == null || StringUtils.isEmpty(serviceCode)) {
      return Mono.just(Res.error("serviceCode不能为空！"))
    }
    val receiveUrl = dto.receiveUrl
    if (receiveUrl == null || StringUtils.isEmpty(receiveUrl)) {
      return Mono.just(Res.error("serviceId=$serviceId ,serviceCode=$serviceCode -> receiveUrl不能为空~"))
    }
    val callbackUrl = dto.callbackUrl
    if (callbackUrl == null || StringUtils.isEmpty(callbackUrl)) {
      return Mono.just(Res.error("serviceId=$serviceId ,serviceCode=$serviceCode -> callbackUrl不能为空~"))
    }
    if (dto.pushType == null) {
      return Mono.just(Res.error("serviceId=$serviceId ,serviceCode=$serviceCode -> pushType不能为空~"))
    }
    val subscriptions = dto.subscribes
    if (!subscriptions.isNullOrEmpty()) {
      subscriptions.forEach { sub ->
        val topic = sub.topic
        if (topic.isNullOrBlank()) {
          return Mono.just(Res.error("serviceId=$serviceId ,serviceCode=$serviceCode -> topic为空~"))
        }
      }
      val distinct = subscriptions.map { it.topic }.toSet()
      if (distinct.size < subscriptions.size) {
        return Mono.just(Res.error("serviceId=$serviceId ,serviceCode=$serviceCode -> 存在重复的topic~"))
      }
    }
    return serviceDetailsRepository.findById(serviceId)
        .defaultIfEmpty(ServiceDetails())
        .map { savedDetails ->
          if (savedDetails.serviceId.isNullOrBlank()) {
            //serviceId是空的，说明库里没有这条数据，需要新增
            AutoSubscribeContext().also {
              it.status = AutoSubscribeContext.Status.Add
              it.serviceDetails = savedDetails
            }
          } else {
            calculateSubscribeInfo(savedDetails, dto)
          }
        }.flatMap { context ->
          if (context.status == AutoSubscribeContext.Status.Add ||
              context.status == AutoSubscribeContext.Status.UpdateSubscribe ||
              context.status == AutoSubscribeContext.Status.UpdateBasicOnly) {
            val serviceDetails = context.serviceDetails!!
            log.debug("serviceCode=${serviceDetails.serviceCode} -> 订阅信息变更~")
            serviceDetailsRepository.save(serviceDetails).map { context }
          } else {
            Mono.just(context)
          }
        }.onErrorResume { e ->
          val context = AutoSubscribeContext()
          when (e) {
            is DuplicateKeyException -> {
              log.warn("为serviceId=${dto.serviceId} 新增订阅失败，serviceCode=${dto.serviceCode} 已存在...")
              context.apply {
                this.success = false
                this.status = AutoSubscribeContext.Status.serviceCodeConflict
                this.message = "为serviceId=${dto.serviceId} 新增订阅失败，serviceCode=${dto.serviceCode} 已存在..."
              }
            }
            is AlterException -> {
              log.debug("自动订阅失败 -> ${e.message}")
              context.apply {
                this.success = false
                this.message = "自动订阅失败 -> ${e::class.java} -> ${e.message}"
              }
            }
            else -> {
              log.debug("自动订阅出现异常：${e.message}")
              context.apply {
                this.success = false
                this.message = "自动订阅出现异常 -> ${e::class.java} -> ${e.message}"
              }
            }
          }
          Mono.just(context)
        }.doOnNext { context ->
          if (context.status == AutoSubscribeContext.Status.Add ||
              context.status == AutoSubscribeContext.Status.UpdateSubscribe) {
            //通知其他节点刷新订阅信息
            refreshNodeSubscriptions()
          }
        }.map { context ->
          if (!context.success) {
            Res.error(context.message)
          } else {
            when (context.status) {
              AutoSubscribeContext.Status.None -> Res.success("配置未发生变更！")
              AutoSubscribeContext.Status.serviceCodeConflict -> Res.error("serviceCode重复 -> serviceCode=${dto.serviceCode}")
              else -> Res.data(context.serviceDetails!!)
            }
          }
        }
  }

  /**
   * 计算需要修改的订阅信息
   * @param savedDetails ServiceDetails - 数据库中已存的订阅信息
   * @param dto AutoSubscribeDto - 新的订阅信息
   * @return AutoSubscribeContext - 自动订阅结果
   * @author 费世程
   * @date 2020/8/12 17:45
   */
  private fun calculateSubscribeInfo(savedDetails: ServiceDetails, dto: AutoSubscribeDto): AutoSubscribeContext {
    val context = AutoSubscribeContext()
    if (savedDetails.serviceCode != dto.serviceCode) {
      log.warn("serviceId=${savedDetails.serviceId} -> serviceCode与原数据不一致...")
      context.status = AutoSubscribeContext.Status.serviceCodeConflict
      return context
    }
    val newDetails = ServiceDetails().apply {
      this.serviceId = dto.serviceId
      this.serviceCode = dto.serviceCode!!
      this.serviceDesc = dto.serviceDesc
      this.receiveUrl = dto.receiveUrl!!
      this.callbackUrl = dto.callbackUrl!!
      try {
        this.pushType = PushType.valueOf(dto.pushType!!)
      } catch (e: Exception) {
        throw AlterException("推送类型pushType=${dto.pushType}不合法! -> serviceId=${savedDetails.serviceId}")
      }
    }
    if (savedDetails.serviceDesc != newDetails.serviceDesc) {
      context.status = AutoSubscribeContext.Status.UpdateBasicOnly
    }
    if (savedDetails.receiveUrl != newDetails.receiveUrl ||
        savedDetails.callbackUrl != newDetails.callbackUrl ||
        savedDetails.pushType != newDetails.pushType) {
      context.status = AutoSubscribeContext.Status.UpdateSubscribe
    }

    val savedSubMapping = savedDetails.subscriptions.associateBy { it.topic }
    for (sub in dto.subscribes) {
      val newSub = Subscription().apply {
        this.topic = sub.topic
        this.active = sub.active
        this.broadcast = sub.broadcast
        this.condition = sub.condition ?: ""
        this.description = sub.subscribeDesc ?: ""
      }
      val savedSub = savedSubMapping[newSub.topic]
      if (savedSub == null) {
        context.status = AutoSubscribeContext.Status.UpdateSubscribe
      } else {
        if (newSub.active != savedSub.active ||
            newSub.broadcast != savedSub.broadcast ||
            newSub.condition != savedSub.condition) {
          context.status = AutoSubscribeContext.Status.UpdateSubscribe
        } else {
          context.status = AutoSubscribeContext.Status.UpdateBasicOnly
        }
      }
      newDetails.subscriptions.add(newSub)
    }
    context.serviceDetails = newDetails
    return context
  }

  private fun refreshNodeSubscriptions() {
    log.debug("订阅变更，通知其他节点刷新订阅信息...")
    cacheService.refreshNodeCache()
  }

}