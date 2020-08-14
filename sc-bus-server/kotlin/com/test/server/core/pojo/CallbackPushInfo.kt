package com.test.server.core.pojo

import com.test.common.message.CallbackMessage
import com.test.server.mongo.document.ServiceDetails

/**
 * @author 费世程
 * @date 2020/8/14 13:53
 */
class CallbackPushInfo(val callbackMessage: CallbackMessage<Any>,
                       val serviceDetails: ServiceDetails) {
  /**
   * 延迟时间
   */
  var delayMillis: Long = -1L
  /**
   * 推送次数
   */
  var pushCount: Int = 0

}