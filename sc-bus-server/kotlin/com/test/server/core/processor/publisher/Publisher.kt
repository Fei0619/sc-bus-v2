package com.test.server.core.processor.publisher

import com.test.server.core.pojo.EventPushInfo
import com.test.server.core.pojo.PublisherRecord
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * @author 费世程
 * @date 2020/8/14 11:51
 */
interface Publisher {

  /**
   * 发布事件
   */
  fun publisherEvent(messages: Flux<EventPushInfo>): Mono<PublisherRecord>

}