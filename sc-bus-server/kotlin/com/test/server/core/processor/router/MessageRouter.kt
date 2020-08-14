package com.test.server.core.processor.router

import com.test.common.message.EventMessage
import com.test.server.core.pojo.EventPushInfo
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * @author 费世程
 * @date 2020/8/13 20:21
 */
interface MessageRouter {
  fun router(messages: Flux<EventMessage<Any>>): Mono<EventPushInfo>
}