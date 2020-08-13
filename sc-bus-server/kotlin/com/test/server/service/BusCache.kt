package com.test.server.service

import com.test.server.mongo.document.ServiceDetails
import com.test.server.pojo.SubscriptionDetails

/**
 * @author 费世程
 * @date 2020/8/12 19:45
 */
interface BusCache {
  fun getTopicSubscriptions(topic: String): List<SubscriptionDetails>?
  fun getSubscriptionDetailsByServiceCode(serviceCode: String): ServiceDetails?
  fun getNodeCount(): Int
}