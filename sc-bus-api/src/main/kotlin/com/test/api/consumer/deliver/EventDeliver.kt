package com.test.api.consumer.deliver


/**
 * @author 费世程
 * @date 2020/7/27 16:54
 */
interface EventDeliver {

  fun deliver(topic: String, message: Any): Any

}