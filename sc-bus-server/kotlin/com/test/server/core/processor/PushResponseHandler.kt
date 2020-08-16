package com.test.server.core.processor

import com.test.server.core.context.PushContext
import com.test.server.core.pojo.CallbackPushInfo
import com.test.server.core.pojo.EventPushInfo
import reactor.core.publisher.Mono

/**
 * 推动结果处理器
 * @author 费世程
 * @date 2020/8/16 14:53
 */
interface PushResponseHandler {

  fun disposeEventResponse(pushContext: PushContext<EventPushInfo>): Mono<Unit>

  fun disposeCallbackResponse(pushContext: PushContext<CallbackPushInfo>): Mono<Unit>

}