package com.test.server.core.context

import com.test.common.constant.PushType
import com.test.common.result.Res

/**
 * @author 费世程
 * @date 2020/8/16 14:55
 */
class PushContext<T>(
    /**
     * 推送数据
     */
    val pushInfo: T,
    /**
     * 推送响应数据
     */
    val responseBody: Res<Any>) {

  /**
   * 是否成功将消息送达消费方
   */
  var delivered: Boolean = true
  /**
   * 推送类型
   */
  var pushType: PushType = PushType.LoadBalance
  /**
   * 响应码
   */
  var responseCode = ""
  /**
   * 响应头
   */
  var responseHeaders: Map<String, List<String>> = emptyMap()
  /**
   * 执行推送的时间戳
   */
  var pushTimestamp = 0L
  /**
   * 推送响应的时间戳
   */
  var responseTimestamp = 0L

}