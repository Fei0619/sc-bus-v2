package com.test.api.consumer.receiver

import com.test.common.message.EventWrapper
import com.test.common.result.Res

/**
 * @author 费世程
 * @date 2020/8/13 14:09
 */
interface EventReceiver {
  fun receiveEvent(event: EventWrapper): Res<Any>
}