package com.test.server.core.processor

import com.test.common.json.JsonUtils
import com.test.common.json.toJsonString
import com.test.server.conf.BusClientProperties
import com.test.server.core.context.PushContext
import com.test.server.core.pojo.CallbackPushInfo
import com.test.server.core.pojo.EventPushInfo
import com.test.server.core.processor.publisher.Publisher
import com.test.server.core.storage.CallbackPushLogStorage
import com.test.server.core.storage.EventPushLogStorage
import com.test.server.pojo.EventPushLog
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.concurrent.ExecutorService

/**
 * @author 费世程
 * @date 2020/8/16 18:19
 */
@Component
class PushResponseHandlerImpl(private val busClientProperties: BusClientProperties,
                              private val reactiveExecutorService: ExecutorService,
                              private val eventPushLogStorage: EventPushLogStorage,
                              private val callbackPushLogStorage: CallbackPushLogStorage,
                              private val callbackProcessor: CallbackProcessor,
                              private val publisher: Publisher) : PushResponseHandler {
  private val log = LoggerFactory.getLogger(PushResponseHandlerImpl::class.java)
  private val minDelayTime = 1

  override fun disposeEventResponse(pushContext: PushContext<EventPushInfo>): Mono<Unit> {
    //1.保存推送记录
    saveEventPushLog(pushContext)

    val delivered = pushContext.delivered
    val pushInfo = pushContext.pushInfo
    val eventMessage = pushInfo.eventMessage
    val subDetails = pushInfo.subscriptionDetails
    //2.1 推送成功 - 执行回调逻辑
    if (delivered) {
      val responseBody = pushContext.responseBody
      if (responseBody.isSuccess()) {
        log.debug("Event delivered success -> eventId=${eventMessage.eventId}，serviceCode=${subDetails.serviceCode}")
      } else {
        log.debug("Event delivered success,but process failed -> eventId=${eventMessage.eventId}," +
            "serviceCode=${subDetails.serviceCode},responseBody=${JsonUtils.toJsonString(responseBody)}")
      }
      return callbackProcessor.callback(pushContext)
    }
    //2.2 推送失败
    val retryLimit = busClientProperties.retryLimit
    val currentRetry = pushInfo.pushCount
    if (currentRetry > retryLimit) {
      //2.2.1 到达最大重试次数，放弃推送，将执行结果回调给事件发布者
      log.debug("Event deliver failed,it has reached max retry times -> eventId=${eventMessage.eventId}," +
          "serviceCode=${subDetails.serviceCode},pushInfo=${JsonUtils.toJsonString(pushInfo)}")
      return callbackProcessor.callback(pushContext)
    }
    //2.2.2 没有到达重试次数，继续尝试
    pushInfo.pushCount = currentRetry + 1
    val delayMillis: Long = calculateNextPushTime(currentRetry).coerceAtLeast(minDelayTime).plus(1000L)
    pushInfo.delayMillis = delayMillis
    log.debug("Event deliver failed,${delayMillis}ms later will retry...")
    return publisher.publisherEvent(Flux.fromArray(arrayOf())).collectList().map { }
  }

  override fun disposeCallbackResponse(pushContext: PushContext<CallbackPushInfo>): Mono<Unit> {
    //1.保存回调推送记录
    //2.1 回调已送达，直接结束
    //2.2 回调未送达
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  //------------------------------------------------ 私有方法 ------------------------------------------------//

  /**
   * 保存事件推送日志
   */
  private fun saveEventPushLog(pushContext: PushContext<EventPushInfo>) {
    val eventPushLog = createEventPushLog(pushContext)
    if (log.isDebugEnabled) {
      log.debug("event push log=${eventPushLog.toJsonString(false)}")
    }
    reactiveExecutorService.execute {
      eventPushLogStorage.saveEventPushLog(eventPushLog).doOnError {
        log.debug("save eventPushLog exception -> ${it.javaClass.name}:${it.message}")
      }.subscribe()
    }
  }

  /**
   * 构造事件推送日志
   */
  private fun createEventPushLog(pushContext: PushContext<EventPushInfo>): EventPushLog {
    return EventPushLog().let {
      it.eventId = pushContext.pushInfo.eventMessage.eventId
      it.topic = pushContext.pushInfo.subscriptionDetails.topic
      it.eventTimestamp = pushContext.pushInfo.eventMessage.timestamp
      it.serviceId = pushContext.pushInfo.subscriptionDetails.serviceId
      it.serviceCode = pushContext.pushInfo.subscriptionDetails.serviceCode
      it.receiveUrl = pushContext.pushInfo.subscriptionDetails.receiveUrl
      it.pushType = pushContext.pushInfo.subscriptionDetails.pushType
      it.realReceiveUrl = pushContext.pushInfo.receiverUrl
      it.realPushType = pushContext.pushInfo.pushType
      it.currentPush = pushContext.pushInfo.pushCount
      it.finalFailure = pushContext.delivered && pushContext.pushInfo.pushCount >= busClientProperties.retryLimit
      it.delivered = pushContext.delivered
      it.responseCode = pushContext.responseCode
      it.responseHeaders = pushContext.responseHeaders
      it.responseBody = pushContext.responseBody
      it.pushTimestamp = pushContext.pushTimestamp
      it.responseTimestamp = pushContext.responseTimestamp
      it.pushElapsedTime = pushContext.responseTimestamp - pushContext.pushTimestamp
      it
    }
  }

  /**
   * 计算下次推送延时
   */
  private fun calculateNextPushTime(currentRetry: Int): Int {
    return (currentRetry * busClientProperties.retryTimeStep) + busClientProperties.retryAwaitSecond
  }

}