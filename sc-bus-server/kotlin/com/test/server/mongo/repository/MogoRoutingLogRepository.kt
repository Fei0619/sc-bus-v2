package com.test.server.mongo.repository

import com.test.server.mongo.document.MongoRoutingLog
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

/**
 * @author 费世程
 * @date 2020/8/14 14:19
 */
@Repository
interface MogoRoutingLogRepository : ReactiveMongoRepository<MongoRoutingLog, String> {

}