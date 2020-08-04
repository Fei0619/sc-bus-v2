package com.test.api.conf

import com.test.api.consumer.subscribe.WebClientSubscribeClient
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Component

/**
 * @author 费世程
 * @date 2020/7/30 10:46
 */
@Component
class BusApiInitializer(private val subscribeClient: WebClientSubscribeClient) : InitializingBean {

  override fun afterPropertiesSet() {
    subscribeClient.autoSubscribe()
  }

}