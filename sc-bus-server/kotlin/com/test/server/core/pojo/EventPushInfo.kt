package com.test.server.core.pojo

import com.test.common.constant.PushType
import com.test.common.message.EventMessage
import com.test.server.pojo.SubscriptionDetails

/**
 * @author 费世程
 * @date 2020/8/13 20:15
 */
class EventPushInfo(val eventMessage: EventMessage<Any>,
                    val subscriptionDetails: SubscriptionDetails) {

  /**
   * 实际推送类型
   */
  var pushType: PushType = subscriptionDetails.pushType
  /**
   * 实际推送地址
   */
  var receiverUrl = subscriptionDetails.receiveUrl
  /**
   * 延迟时间
   */
  var delayMillis: Long = -1L
  /**
   * 推送次数
   */
  var pushCount: Int = 0

}