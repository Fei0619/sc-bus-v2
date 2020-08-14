package com.test.api.publisher

import com.fasterxml.jackson.core.type.TypeReference
import com.test.common.generate.EventGenerator
import com.test.common.message.EventMessage

/**
 * @author 费世程
 * @date 2020/8/13 16:25
 */
interface BasicEventPublisher<T> {

  /**
   * 发布单个事件
   * @param message EventMessage<*>
   * @return T
   * @author 费世程
   * @date 2020/8/13 16:33
   */
  fun publish(message: EventMessage<*>): T

  /**
   * 批量发布多个事件
   * @param messages List<EventMessage<*>>
   * @return T
   * @author 费世程
   * @date 2020/8/13 16:34
   */
  fun batchPublish(messages: List<EventMessage<*>>): T

  fun publishEvent(generator: EventGenerator): T {
    val messages = generator.messages
    return if (messages.size < 2) {
      publish(messages[0])
    } else {
      batchPublish(messages)
    }
  }

}