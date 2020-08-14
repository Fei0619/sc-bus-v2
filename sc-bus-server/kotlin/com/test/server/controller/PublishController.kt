package com.test.server.controller

import com.test.common.json.JsonUtils
import com.test.common.message.EventMessage
import com.test.common.result.PublishResult
import com.test.common.result.Res
import com.test.server.core.processor.MessageReceiver
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.core.publisher.toFlux

/**
 * @author 费世程
 * @date 2020/8/13 16:58
 */
@RestController
@RequestMapping("/publish")
class PublishController(private val messageReceiver: MessageReceiver) {

  private val log = LoggerFactory.getLogger(PublishController::class.java)

  @PostMapping("/one")
  fun publish(eventMessage: EventMessage<Any>): Mono<Res<PublishResult>> {
    val startTimeMillis = System.currentTimeMillis()
    if (log.isDebugEnabled) {
      log.debug("推送事件：EventMessage=${JsonUtils.toJsonString(eventMessage)}")
    }
    return messageReceiver.receiveMessages(listOf(eventMessage).toFlux()).doFinally {
      log.debug("topic=${eventMessage.topic} 事件推送完成,耗时：${System.currentTimeMillis() - startTimeMillis}")
    }
  }

  @PostMapping("/batch")
  fun batchPublish(eventMessages: List<EventMessage<Any>>): Mono<Res<PublishResult>> {
    val startTimeMillis = System.currentTimeMillis()
    if (log.isDebugEnabled) {
      log.debug("推送事件：EventMessage=${JsonUtils.toJsonString(eventMessages)}")
    }
    return messageReceiver.receiveMessages(eventMessages.toFlux()).doFinally {
      log.debug("批量事件推送完成,耗时：${System.currentTimeMillis() - startTimeMillis}")
    }
  }

}