package com.test.api.publisher.impl

import com.test.api.properties.BusClientProperties
import com.test.api.publisher.EventPublisher
import com.test.common.json.JsonUtils
import com.test.common.message.EventMessage
import com.test.common.result.PublishResult
import com.test.common.result.Res
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient

/**
 * @author 费世程
 * @date 2020/8/13 16:39
 */
class WebClientEventPublisher(private val uri: String,
                              private val busClientProperties: BusClientProperties,
                              private val webClientBuilder: WebClient.Builder) : EventPublisher {

  override fun publish(message: EventMessage<*>): Res<PublishResult> {
    val uri = "/publish/one"
    return doPublish(uri, message)
  }

  override fun batchPublish(messages: List<EventMessage<*>>): Res<PublishResult> {
    val uri = "/publish/batch"
    return doPublish(uri, messages)
  }

  fun <T> doPublish(url: String, message: T): Res<PublishResult> {
    val requestUrl = uri + url
    val resStr = webClientBuilder
        .baseUrl(requestUrl)
        .build()
        .post()
        .body(BodyInserters.fromValue(message)).retrieve().bodyToMono(String::class.java).block()
    if (resStr.isNullOrBlank()) {
      return Res.error("事件发布结果为空~")
    }
    return JsonUtils.parseJson(resStr, Res::class.java, PublishResult::class.java)
  }

}