package com.test.api.properties

import com.test.common.constant.PushType
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
  var autoRegister: Boolean = false
  /**
   * bus服务在注册中心的名字
   */
  var busServiceCode: String = "BUS-SERVER"
  /**
   * 服务id
   */
  var serviceId: String = ""
  /**
   * 服务名
   * <p>服务在注册中心的名字，不设置会自动读取spring.application.name配置</p>
   */
  var serviceCode: String? = null
  /**
   * 服务描述
   */
  var serviceDesc: String? = null
  /**
   * 推送地址
   */
  var receiveUrl: String? = null
  /**
   * 回调地址
   */
  var callbackUrl: String? = null
  /**
   * 推送类型
   */
  var pushType: PushType = PushType.LoadBalance
  /**
   * 服务端限流
   */
  var limit: Int = -1
  /**
   * 订阅信息
   */
  var subscribes: List<SubscribeProperties> = ArrayList()

}