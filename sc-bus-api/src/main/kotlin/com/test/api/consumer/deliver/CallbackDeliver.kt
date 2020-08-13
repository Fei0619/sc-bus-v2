package com.test.api.consumer.deliver

import com.test.common.result.Res

/**
 * @author 费世程
 * @date 2020/7/27 16:56
 */
interface CallbackDeliver {
  fun deliverCallback(topic: String, callbackMessage: String): Res<Any>
}