package com.test.api.properties

/**
 * @author 费世程
 * @date 2020/7/30 19:41
 */
class SubscribeProperties {
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