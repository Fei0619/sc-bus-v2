package com.test.server.mongo.document

import com.test.common.constant.PushType
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

/**
 * 消费者详情
 * @author 费世程
 * @date 2020/8/2 17:49
 */
@Document("service_details")
class ServiceDetails {

  /**
   * 服务id
   */
  @Id
  var serviceId: String? = null
  /**
   * 服务码
   */
  @Indexed(name = "service_details_name", unique = true)
  var serviceCode: String = ""
  /**
   * 服务描述
   */
  var serviceDesc: String = ""
  /**
   * 接收推送的地址
   */
  var receiveUrl: String = ""
  /**
   * 接收回调地址
   */
  var callbackUrl: String = ""
  /**
   * url类型
   */
  var pushType: PushType = PushType.LoadBalance
  /**
   * 服务端限流
   */
  var limit: Int = -1
  /**
   * 服务订阅信息
   */
  var subscriptions: MutableList<Subscription> = ArrayList()

}