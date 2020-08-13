package com.test.server.service

import com.test.common.init.Destroyable
import com.test.server.mongo.document.ServiceDetails
import com.test.server.mongo.repository.ServiceDetailsRepository
import com.test.server.pojo.LocalCache
import com.test.server.pojo.SubscriptionDetails
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.netflix.ribbon.SpringClientFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.core.publisher.toFlux
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.TimeUnit
import javax.annotation.PreDestroy

/**
 * @author 费世程
 * @date 2020/8/12 19:23
 */
@Component
class CacheService(private val webClient: WebClient,
                   private val springClientFactory: SpringClientFactory,
                   private val serviceDetailsRepository: ServiceDetailsRepository) : InitializingBean, Destroyable, BusCache {

  private val log = LoggerFactory.getLogger(CacheService::class.java)

  @Value("\${spring.application.name}")
  lateinit var applicationName: String

  //节点刷新缓存的接口地址
  private var refreshCachePath = "cache/refresh"

  private lateinit var currentLocalCache: LocalCache

  // 存储刷新缓存的信号
  private val refreshCacheSignQueue = ArrayBlockingQueue<Boolean>(1)
  private val refreshCacheSign = true

  @Volatile
  private var startRefreshCacheThread = true

  override fun afterPropertiesSet() {
    refreshLocalCache().subscribe()
    Thread {
      while (startRefreshCacheThread) {
        val sign = refreshCacheSignQueue.poll(3, TimeUnit.SECONDS)
        if (sign != null && sign == refreshCacheSign) {
          refreshLocalCache().block()
        }
      }
    }.start()
  }

  /**
   * 通知所有节点刷新缓存
   */
  fun refreshNodeCache(): Mono<Boolean> {
    val loadbalancer = springClientFactory.getLoadBalancer(applicationName)
    if (loadbalancer == null || loadbalancer.allServers == null || loadbalancer.allServers.isEmpty()) {
      log.debug("没有服务在线~")
      return Mono.just(true)
    }
    return loadbalancer.allServers.map { it.hostPort }.toFlux().flatMap {
      log.debug("通知节点${it}刷新缓存...")
      webClient.get().uri("http:/${it}/${refreshCachePath}").retrieve().bodyToMono(ByteArray::class.java)
    }.collectList().map { true }
  }

  /**
   * 刷新缓存
   */
  fun refreshCache() {
    refreshCacheSignQueue.offer(refreshCacheSign)
  }

  override fun getTopicSubscriptions(topic: String): List<SubscriptionDetails>? {
    return currentLocalCache.eventSubscriptionMapper[topic]
  }

  override fun getSubscriptionDetailsByServiceCode(serviceCode: String): ServiceDetails? {
    return currentLocalCache.serviceDetilsMapper[serviceCode]
  }

  override fun getNodeCount(): Int {
    return currentLocalCache.nodeCount
  }

  @PreDestroy
  override fun destroy() {
    startRefreshCacheThread = false
  }

  /**
   * 每隔十分钟刷新一次本地缓存
   */
  @Scheduled(initialDelay = 10 * 60 * 1000, fixedDelay = 10 * 60 * 1000)
  private fun timeToRefresh() {
    refreshLocalCache().block()
  }

  //------------------------------------------- 私有方法 -------------------------------------------//

  /**
   * 刷新本地缓存
   */
  private fun refreshLocalCache(): Mono<Unit> {
    return serviceDetailsRepository.findAll().collectList()
        .map { list ->
          val eventSubscriptionMapper = HashMap<String, MutableList<SubscriptionDetails>>()
          list.forEach { serviceDetails ->
            serviceDetails.subscriptions.map { sub ->
              if (sub.active) {
                sub.topic?.let { topic ->
                  val subscriptionDetails = SubscriptionDetails().apply {
                    this.serviceId = serviceDetails.serviceId!!
                    this.serviceCode = serviceDetails.serviceCode
                    this.receiveUrl = serviceDetails.receiveUrl
                    this.callbackUrl = serviceDetails.callbackUrl
                    this.serviceDesc = serviceDetails.serviceDesc
                    this.topic = topic
                    this.condition = sub.condition
                    this.broadcast = sub.broadcast
                    this.pushType = serviceDetails.pushType
                  }
                  eventSubscriptionMapper.computeIfAbsent(topic) { ArrayList() }.add(subscriptionDetails)
                }
              }
            }
          }
          val serviceDetailsMap = list.associateBy { it.serviceCode }
          val nodeCount = getServerCount()
          LocalCache().apply {
            this.eventSubscriptionMapper = eventSubscriptionMapper
            this.serviceDetilsMapper = serviceDetailsMap
            this.nodeCount = nodeCount
          }
        }
        .map { currentLocalCache = it }
        .doOnNext { log.debug("refresh localCache success...") }
  }

  /**
   * 获取总节点数
   */
  private fun getServerCount(): Int {
    val loadBalancer = springClientFactory.getLoadBalancer(applicationName)
    return loadBalancer?.allServers?.size ?: 0
  }
}