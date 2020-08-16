package com.test.server.core.processor.pusher.http

import com.fasterxml.jackson.core.type.TypeReference
import com.test.common.constant.PushType
import com.test.common.json.JsonUtils
import com.test.common.json.toJsonString
import com.test.common.message.CallbackEventWrapper
import com.test.common.message.EventWrapper
import com.test.common.result.CommonResMsg
import com.test.common.result.Res
import com.test.server.core.context.PushContext
import com.test.server.core.pojo.CallbackPushInfo
import com.test.server.core.pojo.EventPushInfo
import com.test.server.core.pojo.HttpPushResponse
import com.test.server.core.processor.PushResponseHandler
import com.test.server.core.processor.pusher.AbstractMessagePusher
import io.netty.channel.ConnectTimeoutException
import io.netty.handler.timeout.ReadTimeoutException
import io.netty.handler.timeout.WriteTimeoutException
import org.slf4j.LoggerFactory
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.net.ConnectException
import java.net.SocketException

/**
 * @author 费世程
 * @date 2020/8/16 14:52
 */
abstract class WebClientPusher(private val pushResponseHandler: PushResponseHandler,
                               private val pushType: PushType)
  : AbstractMessagePusher(pushType) {
  private val log = LoggerFactory.getLogger(WebClientPusher::class.java)
  private val ANY_RES_TYPE_REFERENCE = object : TypeReference<Res<Any>>() {}

  override fun pushEvent(eventPushInfo: EventPushInfo): Mono<Unit> {
    val pushTimestamp = System.currentTimeMillis()
    val subDetails = eventPushInfo.subscriptionDetails
    val event = eventPushInfo.eventMessage
    return getWebClient().post()
        .uri(subDetails.receiveUrl)
        .body(BodyInserters.fromValue(EventWrapper(subDetails.topic, event.toJsonString(false))))
        .exchange()
        .flatMap { clientResponse ->
          //将原始响应 -> HttpPushResponse
          constructEventResponse(clientResponse, pushTimestamp)
        }.onErrorResume { throwable ->
          //推送异常处理
          Mono.just(pushErrorHandler(throwable, pushTimestamp, subDetails.serviceCode))
        }.map { httpPushResponse ->
          //组装PushContext
          constructPushContext(eventPushInfo, httpPushResponse)
        }.flatMap {
          //处理推送结果
          pushResponseHandler.disposeEventResponse(it)
        }
  }

  override fun pushCallback(callbackPushInfo: CallbackPushInfo): Mono<Unit> {
    val pushTimestamp = System.currentTimeMillis()
    val message = callbackPushInfo.callbackMessage
    val serviceDetails = callbackPushInfo.serviceDetails
    return getWebClient().post()
        .uri(serviceDetails.callbackUrl)
        .body(BodyInserters.fromValue(CallbackEventWrapper(message.topic, message.toJsonString(false))))
        .exchange().flatMap { clientResponse ->
          constructEventResponse(clientResponse, pushTimestamp)
        }.onErrorResume { throwable ->
          Mono.just(pushErrorHandler(throwable, pushTimestamp, serviceDetails.serviceCode))
        }.map { httpPushResponse ->
          constructPushContext(callbackPushInfo, httpPushResponse)
        }.flatMap { pushContext ->
          pushResponseHandler.disposeCallbackResponse(pushContext)
        }
  }

  abstract fun getWebClient(): WebClient

  //----------------------------------------------- 私有方法 ----------------------------------------------//

  /**
   * 将原始响应 -> HttpPushResponse
   */
  private fun constructEventResponse(clientResponse: ClientResponse, pushTimestamp: Long)
      : Mono<HttpPushResponse> {
    val statusCode = clientResponse.statusCode()
    val headers = LinkedHashMap<String, List<String>>()
    val httpHeaders = clientResponse.headers().asHttpHeaders()
    httpHeaders.forEach { k, v ->
      headers[k] = v
    }
    return clientResponse.bodyToMono(String::class.java)
        .defaultIfEmpty("")
        .map { body ->
          HttpPushResponse(body, statusCode.value(), headers.toMap()).also {
            it.pushTimestamp = pushTimestamp
            it.responseTimestamp = System.currentTimeMillis()
          }
        }
  }

  /**
   * 推送异常处理
   */
  private fun pushErrorHandler(t: Throwable, pushTimestamp: Long, serviceCode: String)
      : HttpPushResponse {
    var status = -1
    val message = when (t) {
      is SocketException -> {
        "push SocketException -> ${t.message}"
      }
      is ConnectTimeoutException -> {
        "push exception -> connect timeout"
      }
      is ReadTimeoutException -> {
        "push exception -> read timeout"
      }
      is WriteTimeoutException -> {
        "push exception -> write timeout"
      }
      is ConnectException -> {
        "push exception -> java.net.ConnectException:${t.message}"
      }
      else -> {
        status = 1
        log.error("serviceCode=$serviceCode -> untreated exception - ${t}:${t.message}")
        "push Exception -> ${t}:${t.message}"
      }
    }
    if (status == -1) {
      log.warn(message)
    }
    return HttpPushResponse(message, CommonResMsg.INTERNAL_SERVER_ERROR.code(), emptyMap())
        .also {
          it.pushTimestamp = pushTimestamp
          it.responseTimestamp = System.currentTimeMillis()
        }
  }

  /**
   * 组装PushContext
   */
  private fun <T> constructPushContext(pushInfo: T, httpPushResponse: HttpPushResponse)
      : PushContext<T> {
    val responseBody = if (httpPushResponse.isSuccess()) {
      JsonUtils.parseJson(httpPushResponse.body, ANY_RES_TYPE_REFERENCE)
    } else {
      Res.error(httpPushResponse.body)
    }
    return PushContext(pushInfo, responseBody).also {
      it.deliverd = httpPushResponse.isSuccess()
      it.pushType = pushType
      it.pushTimestamp = httpPushResponse.pushTimestamp
      it.responseHeaders = httpPushResponse.headers
      it.responseTimestamp = httpPushResponse.responseTimestamp
      it.responseCode = httpPushResponse.statusCode.toString()
    }
  }

}