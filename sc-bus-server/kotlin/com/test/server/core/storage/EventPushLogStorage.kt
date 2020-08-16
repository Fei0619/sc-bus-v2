package com.test.server.core.storage

import com.test.server.pojo.EventPushLog
import reactor.core.publisher.Mono

/**
 * @author 费世程
 * @date 2020/8/16 22:54
 */
interface EventPushLogStorage {
  fun saveEventPushLog(eventPushLog: EventPushLog): Mono<Unit>
}