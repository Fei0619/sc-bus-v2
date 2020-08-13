package com.test.server.mongo.document

import org.springframework.data.annotation.Id

/**
 * @author 费世程
 * @date 2020/8/2 18:00
 */
class Subscription {

  /**
   * 主题
   */
  @Id
  var topic: String? = null
  /**
   * 订阅条件
   */
  var condition: String = ""
  /**
   * 是否广播
   */
  var broadcast: Boolean = false
  /**
   * 订阅描述
   */
  var description: String = ""
  /**
   * 是否生效
   */
  var active: Boolean = false

}