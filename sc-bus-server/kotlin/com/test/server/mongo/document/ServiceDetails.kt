package com.test.server.mongo.document

import com.test.common.constant.PushType
import org.springframework.data.mongodb.core.mapping.Document

/**
 * 消费者详情
 * @author 费世程
 * @date 2020/8/2 17:49
 */
@Document("service_details")
class ServiceDetails {

  var serviceId: String? = null
  var serviceCode: String = ""
  var serviceDesc: String? = ""
  var receiveUrl: String = ""
  var callbackUrl:String=""
  var pushType:PushType=PushType.LoadBalance


}