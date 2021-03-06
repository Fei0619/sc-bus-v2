package com.test.api.conf

import com.test.api.consumer.subscribe.SubscribeClient
import com.test.api.consumer.subscribe.WebClientSubscribeClient
import com.test.api.properties.BusClientProperties
import com.test.common.http.WebClients
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

/**
 * @author 费世程
 * @date 2020/7/30 10:47
 */
@Configuration
@EnableConfigurationProperties(BusClientProperties::class)
open class BusApiBeanConfig {

  @Bean
  @LoadBalanced
  @ConditionalOnMissingBean
  open fun loadBalanceWebClientBuilder(): WebClient.Builder {
    return WebClients.createWebClientBuilder(200, 400, 400)
  }

  @Bean
  open fun subscribeClient(busClientProperties: BusClientProperties,
                           loadBalanceWebClientBuilder: WebClient.Builder): SubscribeClient {
    return WebClientSubscribeClient(busClientProperties, loadBalanceWebClientBuilder)
  }

}