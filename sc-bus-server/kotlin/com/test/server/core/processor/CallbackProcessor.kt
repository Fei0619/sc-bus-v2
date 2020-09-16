package com.test.server.core.processor

import com.test.server.core.context.PushContext
import com.test.server.core.pojo.EventPushInfo
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

/**
 * @author 费世程
 * @date 2020/8/19 14:21
 */
@Component
interface CallbackProcessor {

  fun callback(publishContext: PushContext<EventPushInfo>): Mono<Unit>

}