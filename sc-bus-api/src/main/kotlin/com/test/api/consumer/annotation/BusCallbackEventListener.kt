package com.test.api.consumer.annotation

/**
 * @author 费世程
 * @date 2020/7/24 16:57
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class BusCallbackEventListener(val topic: String)