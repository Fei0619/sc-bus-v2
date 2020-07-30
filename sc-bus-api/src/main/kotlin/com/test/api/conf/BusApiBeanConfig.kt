package com.test.api.conf

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

/**
 * @author 费世程
 * @date 2020/7/30 10:47
 */
@Component
@EnableConfigurationProperties(BusClientProperties::class)
class BusApiBeanConfig {

  fun loadBalanceWebClientBuilder(): WebClient.Builder {
//    return WebClient.builder().build()
    TODO()
  }

}