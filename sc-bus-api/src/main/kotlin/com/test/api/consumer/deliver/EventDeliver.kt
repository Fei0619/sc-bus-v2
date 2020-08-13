package com.test.api.consumer.deliver

import com.test.common.result.Res


/**
 * @author 费世程
 * @date 2020/7/27 16:54
 */
interface EventDeliver {

  fun deliverEvent(topic: String, eventMessage: String): Res<Any>

}