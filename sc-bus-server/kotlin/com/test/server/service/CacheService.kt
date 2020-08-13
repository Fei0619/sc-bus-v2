package com.test.server.service

import com.test.common.init.Destroyable
import com.test.server.mongo.repository.ServiceDetailsRepository
import org.springframework.beans.factory.InitializingBean
import org.springframework.cloud.netflix.ribbon.SpringClientFactory
import org.springframework.stereotype.Component
import java.util.concurrent.ExecutorService

/**
 * @author 费世程
 * @date 2020/8/12 19:23
 */
@Component
class CacheService(private val springClientFactory: SpringClientFactory,
                   private val executorService: ExecutorService,
                   private val repository: ServiceDetailsRepository) : InitializingBean, Destroyable {

  override fun afterPropertiesSet() {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  fun refreshNodeCache() {

  }

  override fun destroy() {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}