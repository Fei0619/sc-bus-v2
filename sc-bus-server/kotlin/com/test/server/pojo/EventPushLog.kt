package com.test.server.pojo

import com.test.common.constant.PushType
import com.test.common.json.JsonUtils
import com.test.common.result.Res
import java.time.LocalDateTime

/**
 * @author 费世程
 * @date 2020/8/16 22:36
 */
class EventPushLog {
  /**
   * 创建时间
   */
  var createTime = LocalDateTime.now()

  // ------------------------------------ event 数据 ~~~
  /**
   * 事件id,标记一次事件发布,应保证全局唯一性
   */
  var eventId: String? = null

  /**
   * 事件主题
   */
  var topic: String? = null

  /**
   * 事件产生时间戳
   */
  var eventTimestamp: Long? = null

  // ------------------------------------ 接收方信息 ~~~
  /**
   * 服务id
   */
  var serviceId: String = ""

  /**
   * 服务码
   */
  var serviceCode: String = ""

  /**
   * 接收推送的地址
   */
  var receiveUrl: String = ""

  /**
   * 地址类型
   */
  var pushType: PushType = PushType.LoadBalance


  // ------------------------------------ 推送数据 ~~~
  /**
   * 实际的推送地址
   */
  var realReceiveUrl = ""

  /**
   * 实际的推送类型
   */
  var realPushType: PushType? = null

  /**
   * 当前推送次数
   */
  var currentPush = 0

  /**
   * 最终失败
   * <p>是否最后一次推送且推送失败</p>
   */
  var finalFailure = false

  /**
   * 推送是否成功
   * 只要消费端接收到了消息就算成功
   */
  var delivered = false

  /**
   * 响应码
   */
  var responseCode = ""

  /**
   * 响应头
   */
  var responseHeaders: Map<String, List<String>> = emptyMap()

  /**
   * 推送响应数据
   */
  var responseBody: Res<Any>? = null

  /**
   * 执行推送的时间戳
   */
  var pushTimestamp = 0L

  /**
   * 推送响应的时间戳
   */
  var responseTimestamp = 0L

  /**
   * 推送耗时
   */
  var pushElapsedTime = 0L

  override fun toString(): String {
    return JsonUtils.toJsonString(this)
  }
}