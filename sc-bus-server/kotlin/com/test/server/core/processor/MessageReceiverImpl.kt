package com.test.server.core.processor

import com.test.common.message.EventMessage
import com.test.common.result.PublishResult
import com.test.common.result.Res
import com.test.server.core.processor.filter.MessageFilter
import com.test.server.core.processor.router.MessageRouter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * @author 费世程
 * @date 2020/8/13 18:48
 */
@Component
class MessageReceiverImpl(@Qualifier("idempotentMessageFilter") private val messageFilters: List<MessageFilter>,
                          private val messageRouter: MessageRouter)
  : MessageReceiver {

  override fun receiveMessages(messages: Flux<EventMessage<Any>>): Mono<Res<PublishResult>> {
    //过滤
    var filterMessages = messages
    for (messageFilter in messageFilters) {
      filterMessages = messageFilter.doFilter(filterMessages)
    }

    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

}