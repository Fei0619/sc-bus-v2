package com.test.server.dto

/**
 * @author 费世程
 * @date 2020/8/2 17:46
 */
class AutoSubscribeDto {

  /**
   * 服务id
   */
  var serviceId: String? = null
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
   *
   */
  var pushType: String? = null

  /**
   * 订阅信息
   */
  var subscribes: MutableList<Subscription> = ArrayList()

  class Subscription {
    /**
     * 订阅主题
     */
    var topic: String? = null
    /**
     * 订阅条件
     */
    var condition: String? = null
    /**
     * 是否广播
     */
    var broadcast: Boolean = false
    /**
     * 订阅是否生效
     */
    var active: Boolean = true
    /**
     * 订阅描述
     */
    var subscribeDesc: String? = null
  }

}