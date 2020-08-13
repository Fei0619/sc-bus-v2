package com.test.server.pojo

import com.test.server.mongo.document.ServiceDetails

/**
 * @author 费世程
 * @date 2020/8/13 9:53
 */
class LocalCache {

  /**
   * topic -> List<SubscriptionDetails>
   */
  lateinit var eventSubscriptionMapper: Map<String, MutableList<SubscriptionDetails>>

  /**
   * serviceCode -> SubscriptionDetails
   */
  lateinit var serviceDetilsMapper: Map<String, ServiceDetails>

  /**
   * 节点数
   */
  var nodeCount: Int = 1

}