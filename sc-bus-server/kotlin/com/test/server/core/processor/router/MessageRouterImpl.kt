package com.test.server.core.processor.router

import com.test.common.constant.PushType
import com.test.common.json.JsonUtils
import com.test.common.message.EventMessage
import com.test.server.core.pojo.EventPushInfo
import com.test.server.pojo.SubscriptionDetails
import com.test.server.service.CacheService
import org.apache.commons.lang.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.cloud.netflix.ribbon.SpringClientFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

/**
 * @author 费世程
 * @date 2020/8/13 20:23
 */
@Component
class MessageRouterImpl(private val cacheService: CacheService,
                        private val springClientFactory: SpringClientFactory) : MessageRouter {
  private val log = LoggerFactory.getLogger(MessageRouterImpl::class.java)

  /**
   * 计算推送路由
   */
  override fun router(messages: Flux<EventMessage<Any>>): Flux<EventPushInfo> {
    return messages.flatMap { message ->
      val topic = message.topic
      val subscriptions = cacheService.getTopicSubscriptions(topic)
      if (subscriptions.isNullOrEmpty()) {
        log.debug("topic=${topic} -> 没有订阅信息~")
        emptyList<EventPushInfo>().toMono()
      } else {
        if (log.isDebugEnabled) {
          log.debug("topic=${topic} -> 订阅信息：${JsonUtils.toJsonString(subscriptions)}")
        }
        val pushMessages = ArrayList<EventPushInfo>()
        for (sub in subscriptions) {
          //订阅条件
          val conditionsGroup = sub.getConditionsGroup()
          if (ConditionMatcher.match(conditionsGroup, message.headers)) {
            //符合条件，推送
            val pushInfos = convertEventPushInfo(message, sub)
            pushMessages.addAll(pushInfos)
          } else {
            log.debug("推送条件判断不通过：topic=${message.topic},subscription=${JsonUtils.toJsonString(sub)}")
          }
        }
        if (log.isDebugEnabled) {
          log.debug("topic=${message.topic}, router=${JsonUtils.toJsonString(pushMessages)}")
        }
        Mono.just(pushMessages)
      }
    }.flatMapIterable { it } // Flux<List<PushMessage>> -> Flux<PushMessage>
  }

  //----------------------------------------------- 私有方法 ----------------------------------------------//

  private fun convertEventPushInfo(message: EventMessage<Any>, subscription: SubscriptionDetails): List<EventPushInfo> {
    val pushInfo = EventPushInfo(message, subscription)
    pushInfo.delayMillis = message.delayMillis
    val pushInfoList: MutableList<EventPushInfo> = mutableListOf(pushInfo)
    if (subscription.broadcast) {
      //广播:以广播模式订阅，会将消息广播发送给所有的节点
      if (subscription.pushType == PushType.LoadBalance) {
        val split = StringUtils.split(subscription.receiveUrl, "/", 3)
        val loadBalanced = springClientFactory.getLoadBalancer(split[1])
        if (loadBalanced != null) {
          val servers = loadBalanced.allServers
          for (server in servers) {
            val receiverUrl = "http://${server.host}:${server.port}/${split[2]}"
            val item = EventPushInfo(message, subscription).apply {
              this.delayMillis = message.delayMillis
              this.pushType = PushType.Host
              this.receiverUrl = receiverUrl
            }
            pushInfoList.add(item)
          }
        }
      } else {
        log.debug("serviceCode=${subscription.serviceCode},topic=${subscription.topic},pushType=${subscription.pushType} -> 不支持广播模式~")
      }
    }
    return pushInfoList
  }

}