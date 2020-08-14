package com.test.server.pojo

import com.test.common.message.EventHeaders
import java.time.LocalDateTime

/**
 * @author 费世程
 * @date 2020/8/14 14:16
 */
class RoutingLog {

  /**
   * 创建时间
   */
  var createTime = LocalDateTime.now()

  // ------------------------------------ event 数据 ~~~
  /**
   * 事件id,标记一次事件发布,应保证全局唯一性
   */
  var eventId: String? = null

  /**
   * 事件主题
   */
  var topic: String? = null

  /**
   * 消息头,用于条件匹配
   */
  var headers: EventHeaders? = null

  /**
   * 发送方幂等key,如果该字段不为空,则发送消息时会在一段时间内根据该值执行幂等
   */
  var idempotentKey: String? = null

  /**
   * 延迟消息需要延迟的时间(ms),默认不延迟
   */
  var delayMillis: Long? = null

  /**
   * 消息内容
   */
  var payload: Any? = null

  /**
   * 是否需要回调消费结果
   */
  var callback = false

  /**
   * 事件产生时间戳
   */
  var timestamp: Long? = null

  /**
   * 订阅信息列表
   */
  var subscriptions = emptyList<SubscriptionDetails>()
}