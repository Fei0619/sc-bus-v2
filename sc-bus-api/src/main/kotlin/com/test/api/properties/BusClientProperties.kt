package com.test.api.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * @author 费世程
 * @date 2020/7/30 10:51
 */
@Component
@ConfigurationProperties(prefix = "bus.client")
class BusClientProperties {

  /**
   * 是否自动订阅
   */
  private var autoRegister: Boolean? = false
  /**
   * bus服务code
   */
  private var busServiceCode: String? = null
  /**
   * 服务名
   */
  private var serviceCode: String? = null
  /**
   * 服务描述
   */
  private var serviceDesc: String? = null
  /**
   * 推送地址
   */
  private var receiveUrl: String? = null
  /**
   * 回调地址
   */
  private var callbackUrl: String? = null
  /**
   * 推送类型
   */
  private var pushType: String? = null
  /**
   * 订阅信息
   */
  private var subscribes: List<SubscribeProperties>? = null

}