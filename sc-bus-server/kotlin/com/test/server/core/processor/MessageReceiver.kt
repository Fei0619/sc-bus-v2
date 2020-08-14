package com.test.server.core.processor

import com.test.common.message.EventMessage
import com.test.common.result.PublishResult
import com.test.common.result.Res
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * 接收器 - 接收客户端发送的事件
 * @author 费世程
 * @date 2020/8/13 17:57
 */
interface MessageReceiver {
  fun receiveMessages(messages: Flux<EventMessage<Any>>): Mono<Res<PublishResult>>
}