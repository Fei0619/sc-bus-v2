package com.test.api.consumer.receiver

import com.test.common.message.CallbackEventWrapper
import com.test.common.result.Res

/**
 * @author 费世程
 * @date 2020/8/13 14:06
 */
interface CallbackReceiver {
  fun receiveCallback(callback: CallbackEventWrapper): Res<Any>
}