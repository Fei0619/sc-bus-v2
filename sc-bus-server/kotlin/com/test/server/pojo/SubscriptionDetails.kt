package com.test.server.pojo

import com.test.common.constant.PushType

/**
 * @author 费世程
 * @date 2020/8/12 19:48
 */
class SubscriptionDetails {
  /**
   * 服务id
   */
  var serviceId: String = ""

  /**
   * 服务码
   */
  var serviceCode: String = ""

  /**
   * 服务描述
   */
  var serviceDesc: String = ""

  /**
   * 接收推送的地址
   */
  var receiveUrl: String = ""

  /**
   * 接收回调的地址
   */
  var callbackUrl: String? = null

  /**
   * 地址类型
   */
  var pushType: PushType = PushType.LoadBalance

  /**
   * 订阅主题
   */
  var topic: String = ""

  /**
   * 订阅条件
   */
  var condition: String = ""

  /**
   * 服务端限流
   */
  var limit = -1

  /**
   * 是否广播
   */
  var broadcast = false

}