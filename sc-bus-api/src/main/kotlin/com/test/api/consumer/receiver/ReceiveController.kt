package com.test.api.consumer.receiver

import com.test.api.constant.BusUrl
import com.test.api.consumer.deliver.CallbackDeliver
import com.test.api.consumer.deliver.EventDeliver
import com.test.common.message.CallbackEventWrapper
import com.test.common.message.EventWrapper
import com.test.common.result.Res
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author 费世程
 * @date 2020/8/13 14:10
 */
@RestController
class ReceiveController(private val eventDeliver: EventDeliver,
                        private val callbackDeliver: CallbackDeliver) : EventReceiver, CallbackReceiver {

  private val log = LoggerFactory.getLogger(ReceiveController::class.java)

  @PostMapping(BusUrl.CLIENT_EVENT_RECEIVE_URL)
  override fun receiveEvent(event: EventWrapper): Res<Any> {
    val topic = event.topic
    val message = event.eventMessage
    log.debug("接收到一个事件：topic=$topic -> eventMessage=$message")
    return eventDeliver.deliverEvent(topic, message)
  }

  @PostMapping(BusUrl.CLIENT_CALLBACK_RECEIVE_URL)
  override fun receiveCallback(callback: CallbackEventWrapper): Res<Any> {
    val topic = callback.topic
    val message = callback.callbackMessage
    log.debug("接收到一个事件回调：topic=$topic -> callbackMessage=$message")
    return callbackDeliver.deliverCallback(topic, message)
  }

}