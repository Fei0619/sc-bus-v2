package com.test.server.core.processor.pusher

import com.test.common.constant.PushType
import com.test.server.core.pojo.CallbackPushInfo
import com.test.server.core.pojo.EventPushInfo
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono

/**
 * @author 费世程
 * @date 2020/8/14 17:37
 */
interface MessagePusher {

  companion object {
    private val log = LoggerFactory.getLogger(MessagePusher::class.java)
    private val pusherMapping = HashMap<PushType, MessagePusher>()

    fun register(pushType: PushType, messagePusher: MessagePusher) {
      if (pusherMapping[pushType] != null) {
        log.warn("$pushType -> pushType冲突...")
      }
      pusherMapping.putIfAbsent(pushType, messagePusher)
    }

    fun getMessagePusher(pushType: PushType): MessagePusher {
      return pusherMapping[pushType] ?: throw Error("$pushType -> 对应的MessagePusher不存在")
    }

  }

  fun pushEvent(event: EventPushInfo): Mono<Unit>

  fun pushCallback(callbackEvent: CallbackPushInfo): Mono<Unit>

}