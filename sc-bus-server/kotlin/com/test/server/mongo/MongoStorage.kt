package com.test.server.mongo

import com.test.server.core.storage.EventPushLogStorage
import com.test.server.core.storage.RoutingStorage
import com.test.server.mongo.repository.MogoRoutingLogRepository
import com.test.server.pojo.EventPushLog
import com.test.server.pojo.RoutingLog
import reactor.core.publisher.Mono

/**
 * @author 费世程
 * @date 2020/8/14 14:14
 */
class MongoStorage(private val routingLogRepository: MogoRoutingLogRepository)
  : RoutingStorage,
    EventPushLogStorage {

  override fun saveRoutingLogs(routingLogs: List<RoutingLog>): Mono<Unit> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun saveEventPushLog(eventPushLog: EventPushLog): Mono<Unit> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}