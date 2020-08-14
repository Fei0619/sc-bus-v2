package com.test.server.core.storage

import com.test.server.pojo.RoutingLog
import reactor.core.publisher.Mono

/**
 * @author 费世程
 * @date 2020/8/14 14:10
 */
interface RoutingStorage {

  fun saveRoutingLogs(routingLogs: List<RoutingLog>): Mono<Unit>

}