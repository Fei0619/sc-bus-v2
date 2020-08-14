package com.test.server.core.processor.pusher

import com.test.common.constant.PushType
import org.springframework.beans.factory.InitializingBean

/**
 * @author 费世程
 * @date 2020/8/14 17:39
 */
abstract class AbstractMessagePusher(private val pushType: PushType) : MessagePusher, InitializingBean {

  override fun afterPropertiesSet() {
    MessagePusher.register(pushType, this)
  }

}