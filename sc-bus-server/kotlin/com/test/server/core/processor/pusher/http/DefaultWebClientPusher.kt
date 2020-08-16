package com.test.server.core.processor.pusher.http

import com.test.common.constant.PushType
import com.test.server.core.processor.PushResponseHandler
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

/**
 * @author 费世程
 * @date 2020/8/16 18:15
 */
@Component(value = "DefaultWebClientPusher")
class DefaultWebClientPusher(pushResponseHandler: PushResponseHandler,
                             private val webClient: WebClient)
  : WebClientPusher(pushResponseHandler, PushType.Host) {

  override fun getWebClient(): WebClient {
    return webClient
  }

}