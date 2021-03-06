package com.test.server.conf

import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.test.common.http.WebClients
import com.test.server.core.processor.filter.IdempotentMessageFilter
import com.test.server.core.processor.filter.LocalIdempotentMessageFilter
import com.test.server.core.processor.filter.MessageFilter
import com.test.server.core.processor.publisher.AsyncPublisher
import com.test.server.core.processor.publisher.Publisher
import com.test.server.core.storage.CallbackPushLogStorage
import com.test.server.core.storage.EventPushLogStorage
import com.test.server.core.storage.RoutingStorage
import com.test.server.mongo.MongoStorage
import com.test.server.mongo.repository.MogoRoutingLogRepository
import com.test.server.service.CacheService
import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import java.util.concurrent.*

/**
 * @author 费世程
 * @date 2020/8/12 19:50
 */
@Configuration
open class BusServerBeanConfig(private val busClientProperties: BusClientProperties) {

  @Bean
  open fun webClient(): WebClient {
    return WebClients.createWebClient(
        busClientProperties.webClientConnectTimeout.toMillis().toInt(),
        busClientProperties.webClientReadTimeout.toMillis(),
        busClientProperties.webClientWriteTimeout.toMillis())
  }

  @Bean
  @LoadBalanced
  open fun loadBalancedWebClientBuilder(): WebClient.Builder {
    return WebClients.createWebClientBuilder(
        busClientProperties.webClientConnectTimeout.toMillis().toInt(),
        busClientProperties.webClientReadTimeout.toMillis(),
        busClientProperties.webClientWriteTimeout.toMillis())
  }

  /**
   * ThreadPoolExecutor(
   *  int corePoolSize,    //线程池的核心线程数
   *  int maximumPoolSize, //最大线程数
   *  long keepAliveTime,  //线程池空闲线程存活时长
   *  TimeUnit unit,       //存活时长时间单位
   *  BlockingQueue<Runnable> workQueue, //存放任务的队列
   *  ThreadFactory threadFactory        //线程池创建线程的工厂
   *  RejectedExecutionHandler handler   //拒绝任务策略
   * )
   */
  @Bean
  open fun executorService(): ExecutorService {
    val corePoolSize = Runtime.getRuntime().availableProcessors()
    return ThreadPoolExecutor(
        corePoolSize,
        0,
        60, TimeUnit.SECONDS,
        SynchronousQueue(),
        ThreadFactoryBuilder().build(),
        ThreadPoolExecutor.CallerRunsPolicy() //拒绝任务策略：由向线程池提交任务的线程执行
    )
//    return Executors.newCachedThreadPool()
  }

  @Bean
  open fun idempotentMessageFilter(): IdempotentMessageFilter {
    return LocalIdempotentMessageFilter(busClientProperties)
  }

  @Bean("idempotentMessageFilter")
  open fun messageFilters(idempotentMessageFilter: IdempotentMessageFilter): List<MessageFilter> {
    return listOf(idempotentMessageFilter)
  }

  @Bean
  open fun asyncPublisher(cacheService: CacheService,
                          executorService: ExecutorService): Publisher {
    return AsyncPublisher(cacheService, executorService)
  }

  @Bean
  open fun mongoStorage(routingLogRepository: MogoRoutingLogRepository): MongoStorage {
    return MongoStorage(routingLogRepository)
  }

  @Bean
  open fun routingStorage(mongoStorage: MongoStorage): RoutingStorage {
    return mongoStorage
  }

  @Bean
  open fun eventPushLogStorage(mongoStorage: MongoStorage): EventPushLogStorage {
    return mongoStorage
  }

  @Bean
  open fun callbackPushLogStorage(mongoStorage: MongoStorage): CallbackPushLogStorage {
    return mongoStorage
  }

}