package com.test.server.core.processor.publisher

import com.test.server.core.pojo.CallbackPushInfo
import com.test.server.core.pojo.EventPushInfo
import com.test.server.core.pojo.PublishRecord
import com.test.server.service.BusCache
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.concurrent.ExecutorService

/**
 * @author 费世程
 * @date 2020/8/14 13:56
 */
class AsyncPublisher(private val busCache: BusCache,
                     private val reactiveExecutor: ExecutorService) : Publisher {

  override fun publisherEvent(eventPushInfos: Flux<EventPushInfo>): Flux<PublishRecord> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun pushCallback(callbackPushInfos: Flux<CallbackPushInfo>): Mono<Unit> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

}