package com.test.server.core.processor.filter

import com.test.common.message.EventMessage
import reactor.core.publisher.Flux

/**
 * @author 费世程
 * @date 2020/8/13 19:08
 */
interface MessageFilter {
  fun doFilter(eventMessages: Flux<EventMessage<Any>>): Flux<EventMessage<Any>>
}