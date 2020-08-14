package com.test.server.core.processor

import com.test.common.json.JsonUtils
import com.test.common.message.EventMessage
import com.test.common.result.PublishResult
import com.test.common.result.Res
import com.test.server.core.pojo.PublishRecord
import com.test.server.core.processor.filter.MessageFilter
import com.test.server.core.processor.publisher.Publisher
import com.test.server.core.processor.router.MessageRouter
import com.test.server.core.storage.RoutingStorage
import com.test.server.pojo.RoutingLog
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * @author 费世程
 * @date 2020/8/13 18:48
 */
@Component
class MessageReceiverImpl(@Qualifier("idempotentMessageFilter") private val messageFilters: List<MessageFilter>,
                          private val messageRouter: MessageRouter,
                          private val publisher: Publisher,
                          private val reactiveExecutors: ExecutorService,
                          private val routingStorage: RoutingStorage)
  : MessageReceiver {
  private val log = LoggerFactory.getLogger(MessageReceiverImpl::class.java)

  override fun receiveMessages(messages: Flux<EventMessage<Any>>): Mono<Res<PublishResult>> {
    //1.过滤
    var filterMessages = messages
    for (messageFilter in messageFilters) {
      filterMessages = messageFilter.doFilter(filterMessages)
    }
    //2.计算推送路径
    val pushInfos = messageRouter.router(messages)
    //3.推送
    return publisher.publisherEvent(pushInfos).collectList().doOnNext {
      //保存推送日志
      saveRoutingLog(it)
    }.map {
      Res.success<PublishResult>()
    }
  }

  //------------------------------------------- 私有方法 -----------------------------------------------//

  private fun saveRoutingLog(publishRecords: List<PublishRecord>) {
    val routingLogs = convertRoutingLog(publishRecords)
    reactiveExecutors.execute {
      routingStorage.saveRoutingLogs(routingLogs).doOnError {
        log.warn("save routing log failed -> routingLog:${JsonUtils.toJsonString(routingLogs)}")
      }
    }
  }

  private fun convertRoutingLog(publishRecords: List<PublishRecord>): List<RoutingLog> {
    val logs = ArrayList<RoutingLog>()
    publishRecords.groupBy { it.pushInfo.eventMessage.eventId }
        .forEach { (_, record) ->
          val message = record[0].pushInfo.eventMessage
          val subscriptions = record.map {
            it.pushInfo.subscriptionDetails
          }.distinctBy {
            it.serviceId
          }
          val routingLog = RoutingLog().apply {
            this.eventId = message.eventId
            this.topic = message.topic
            this.delayMillis = message.delayMillis
            this.headers = message.headers
            this.idempotentKey = message.idempotentKey
            this.payload = message.payload
            this.timestamp = message.timestamp
            this.callback = message.callback
            this.subscriptions = subscriptions
          }
          logs.add(routingLog)
        }
    return logs
  }

}