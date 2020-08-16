package com.test.server.core.processor.pusher.http

import com.test.common.constant.PushType
import com.test.server.core.processor.PushResponseHandler
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

/**
 * @author 费世程
 * @date 2020/8/16 22:15
 */
@Component("LoadBalancedWebClientPusher")
class LoadBalancedWebClientPusher(pushResponseHandler: PushResponseHandler,
                                  private val loadBalancedWebClientBuilder: WebClient.Builder)
  : WebClientPusher(pushResponseHandler, PushType.LoadBalance) {
  override fun getWebClient(): WebClient {
    return loadBalancedWebClientBuilder.build()
  }

}