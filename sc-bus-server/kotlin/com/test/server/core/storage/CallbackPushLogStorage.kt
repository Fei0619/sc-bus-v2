package com.test.server.core.storage

import com.test.server.core.pojo.CallbackPushInfo
import reactor.core.publisher.Mono

/**
 * @author 费世程
 * @date 2020/8/19 15:04
 */
interface CallbackPushLogStorage {
  fun saveCallbackPushLog(callbackPushInfo: CallbackPushInfo): Mono<Unit>
}