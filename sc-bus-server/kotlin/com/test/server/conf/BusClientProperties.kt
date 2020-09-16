package com.test.server.conf

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.time.Duration

/**
 * @author 费世程
 * @date 2020/8/13 10:38
 */
@ConfigurationProperties(prefix = "bus.client")
@Component
class BusClientProperties {

  /**
   * 建立连接超时时间
   */
  val webClientConnectTimeout = Duration.ofMillis(200)!!
  /**
   * 写入超时时间
   */
  val webClientWriteTimeout = Duration.ofMillis(400)!!
  /**
   * 读取超时时间
   */
  val webClientReadTimeout = Duration.ofMillis(400)!!
  /**
   * 幂等过期时间 - 单位:秒
   */
  val idempotentExpireTime = 60L
  /**
   * 默认的失败重试次数
   */
  val retryLimit = 2
  /**
   * 失败重试时间间隔
   */
  val retryAwaitSecond: Int = 5
  /**
   * 失败重试时间步长
   */
  val retryTimeStep: Int = 2

}