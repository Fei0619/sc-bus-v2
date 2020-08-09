package com.test.server.mongo.repository

import com.test.server.mongo.document.ServiceDetails
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

/**
 * @author 费世程
 * @date 2020/8/10 0:25
 */
@Repository
interface ServiceDetailsRepository : ReactiveMongoRepository<ServiceDetails, String>{

}