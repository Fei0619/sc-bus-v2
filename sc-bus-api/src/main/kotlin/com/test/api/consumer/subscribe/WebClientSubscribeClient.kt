package com.test.api.consumer.subscribe

import com.test.api.properties.BusClientProperties
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.reactive.function.client.WebClient

/**
 * @author 费世程
 * @date 2020/7/30 19:24
 */
class WebClientSubscribeClient(private val busClientProperties: BusClientProperties,
                               private val webClientBuilder: WebClient.Builder) : SubscribeClient {

  private val log = LoggerFactory.getLogger(WebClientSubscribeClient::class.java)

//  @Value(value = "${spring.applicationName.name}")
//  private val applicationName: String = ""

  override fun autoSubscribe() {
    if (!busClientProperties.autoRegister) {
      log.debug("The auto subscribe is off...")
    }

    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}