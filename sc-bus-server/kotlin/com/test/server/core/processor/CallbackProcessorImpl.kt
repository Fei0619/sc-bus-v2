package com.test.server.core.processor

import com.test.server.core.context.PushContext
import com.test.server.core.pojo.EventPushInfo
import reactor.core.publisher.Mono

/**
 * @author 费世程
 * @date 2020/8/19 14:22
 */
class CallbackProcessorImpl : CallbackProcessor {

  override fun callback(publishContext: PushContext<EventPushInfo>): Mono<Unit> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

}