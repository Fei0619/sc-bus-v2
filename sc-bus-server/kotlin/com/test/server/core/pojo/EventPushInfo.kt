package com.test.server.core.pojo

import com.test.common.constant.PushType
import com.test.common.message.EventMessage
import com.test.server.pojo.SubscriptionDetails

/**
 * @author 费世程
 * @date 2020/8/13 20:15
 */
class EventPushInfo(eventMessage: EventMessage<Any>, subscriptionDetails: SubscriptionDetails) {

  /**
   * 实际推送类型
   */
  val pushType: PushType = subscriptionDetails.pushType
  /**
   * 实际推送地址
   */
  val receiverUrl = subscriptionDetails.receiveUrl
  /**
   * 延迟时间
   */
  val delayMillis: Long? = null
  /**
   * 推送次数
   */
  val pushCount: Int = 0

}