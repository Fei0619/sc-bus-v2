package com.test.server.service

import com.test.common.result.Res
import com.test.server.context.AutoSubscribeContext
import com.test.server.dto.AutoSubscribeDto
import com.test.server.mongo.document.ServiceDetails
import com.test.server.mongo.repository.ServiceDetailsRepository
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import reactor.core.publisher.Mono

/**
 * @author 费世程
 * @date 2020/8/2 17:43
 */
@Service
class SubscribeService(private val serviceDetailsRepository: ServiceDetailsRepository) {

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
    serviceDetailsRepository.findById(serviceId)
        .defaultIfEmpty(ServiceDetails())
        .map { serviceDetails ->
          if (serviceDetails.serviceId.isNullOrBlank()) {
            //serviceId是空的，说明库里没有这条数据，需要新增
            AutoSubscribeContext().let {
              it.status = AutoSubscribeContext.Status.Add
              it.serviceDetails = serviceDetails
            }
          } else {

          }
        }
    TODO()
  }

}