package com.test.api.annotation

/**
 * @author 费世程
 * @date 2020/7/24 16:50
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class BusEventListener(val topic: String)