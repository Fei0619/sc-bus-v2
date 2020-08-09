package com.test.api.consumer.subscribe

import com.test.api.constant.BusUrl
import com.test.api.properties.BusClientProperties
import com.test.common.constant.PushType
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.util.StringUtils
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import java.util.stream.Collectors

/**
 * @author 费世程
 * @date 2020/7/30 19:24
 */
class WebClientSubscribeClient(private val busClientProperties: BusClientProperties,
                               private val webClient: WebClient.Builder) : SubscribeClient {

  private val log = LoggerFactory.getLogger(WebClientSubscribeClient::class.java)

  @Value(value = "\${spring.applicationName.name}")
  private val applicationName = ""

  override fun autoSubscribe() {
    //1.自动订阅关闭则不做处理
    if (!busClientProperties.autoRegister) {
      log.debug("The auto subscribe is off...")
      return
    }
    val serviceCode = busClientProperties.serviceCode
    if (StringUtils.isEmpty(serviceCode)) {
      busClientProperties.serviceCode = applicationName
    }
    //2.构造LoadBalance的接收事件和回调地址
    val isLoadBalance = busClientProperties.pushType == PushType.LoadBalance
    val receiveUrl = busClientProperties.receiveUrl
    val callbackUrl = busClientProperties.callbackUrl
    if (StringUtils.isEmpty(receiveUrl) && isLoadBalance) {
      busClientProperties.receiveUrl = "http://${applicationName}/${BusUrl.CLIENT_EVENT_RECEIVE_URL}"
    } else {
      log.warn("receive url is empty in BusClientProperties...")
    }
    if (StringUtils.isEmpty(callbackUrl) && isLoadBalance) {
      busClientProperties.callbackUrl = "http://$applicationName/${BusUrl.CLIENT_CALLBACK_RECEIVE_URL}"
    } else {
      log.warn("callback url is empty in BusClientProperties...")
    }
    //3.过滤topic为空的订阅信息
    val subscriptions = busClientProperties.subscribes
    val collect = subscriptions.stream().filter {
      val empty = StringUtils.isEmpty(it.topic)
      if (empty) {
        log.debug("topic is empty in SubscribeProperties ~")
      }
      !empty
    }.collect(Collectors.toList())
    if (collect.size < subscriptions.size) busClientProperties.subscribes = collect
    //4.保存自动订阅信息
    val busServiceCode = busClientProperties.busServiceCode
    val saveSubscribeInfoUrl = "http://$busServiceCode/subscribe/autoSubscription"
    webClient.build()
        .post()
        .uri(saveSubscribeInfoUrl)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(busClientProperties))
        .retrieve()
        .bodyToMono(String::class.java)
        .doOnError {
          log.error("自动订阅出现异常 -> ${it.message}")
        }.subscribe { s -> log.info("自动订阅结果：$s") }
  }
}