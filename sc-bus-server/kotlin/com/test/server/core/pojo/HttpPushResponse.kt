package com.test.server.core.pojo

import org.springframework.http.HttpStatus
import java.beans.Transient

/**
 * @author 费世程
 * @date 2020/8/16 16:13
 */
class HttpPushResponse(
    /**
     * 响应消息体
     */
    val body: String,
    /**
     * 响应状态码
     */
    val statusCode: Int,
    /**
     * 响应头
     */
    val headers: Map<String, List<String>>) {

  /**
   * 执行推送时间戳
   */
  var pushTimestamp = 0L
  /**
   * 执行响应时间戳
   */
  var responseTimestamp = 0L

  @Transient
  fun isSuccess(): Boolean {
    return statusCode == HttpStatus.OK.value()
  }

}