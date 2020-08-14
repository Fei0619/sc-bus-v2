package com.test.server.core.processor.filter

import com.github.benmanes.caffeine.cache.Caffeine
import com.test.common.message.EventMessage
import com.test.server.conf.BusClientProperties
import org.springframework.util.StringUtils
import reactor.core.publisher.Flux
import java.util.concurrent.TimeUnit

/**
 * @author 费世程
 * @date 2020/8/13 19:11
 */
class LocalIdempotentMessageFilter(private val busClientProperties: BusClientProperties) : IdempotentMessageFilter {

  private val maximumSize = 1L.shl(20)
  private val cache = Caffeine.newBuilder()
      .maximumSize(maximumSize)
      .expireAfterAccess(busClientProperties.idempotentExpireTime, TimeUnit.SECONDS)
      .build<String, Byte>()

  override fun doFilter(eventMessages: Flux<EventMessage<Any>>): Flux<EventMessage<Any>> {
    return eventMessages.map { message ->
      var existence = true
      val idempotentKey = message.idempotentKey
      if (!StringUtils.isEmpty(idempotentKey)) {
        cache.get(idempotentKey) {
          existence = false
          0
        }
      } else {
        existence = true
      }
      Pair(existence, message)
    }.filter { pair -> !pair.first }.map { it.second }
  }
}