package com.test.server.core.processor.router

import com.test.common.json.JsonUtils
import com.test.common.message.EventMessage
import com.test.server.core.pojo.EventPushInfo
import com.test.server.service.CacheService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono

/**
 * @author 费世程
 * @date 2020/8/13 20:23
 */
@Component
class MessageRouterImpl(private val cacheService: CacheService) : MessageRouter {
  private val log = LoggerFactory.getLogger(MessageRouterImpl::class.java)

  override fun router(messages: Flux<EventMessage<Any>>): Mono<EventPushInfo> {
    messages.map { message ->
      val topic = message.topic
      val subscriptions = cacheService.getTopicSubscriptions(topic)
      if (subscriptions.isNullOrEmpty()) {
        log.debug("topic=${topic} -> 没有订阅信息~")
        emptyList<MessageRouter>().toMono()
      } else {
        if (log.isDebugEnabled) {
          log.debug("topic=${topic} -> 订阅信息：${JsonUtils.toJsonString(subscriptions)}")
        }
        for (sub in subscriptions) {

        }

      }
    }


    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

}