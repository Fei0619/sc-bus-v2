package com.test.common.message;


import lombok.Data;

import java.util.UUID;

/**
 * @author 费世程
 * @date 2020/8/13 14:47
 */
@Data
public class EventMessage<T> {

  /**
   * 事件id
   */
  private String eventId = UUID.randomUUID().toString();
  /**
   * 主题
   */
  private String topic;
  /**
   * 消息体
   */
  private T payload;
  /**
   * 条件
   */
  private EventHeaders headers = EventHeaders.create();
  /**
   * 发送方幂等key
   */
  private String idempotentKey;
  /**
   * 延迟时间：ms
   */
  private Long delayMillis = -1L;
  /**
   * 是否需要回调
   */
  private Boolean callback;
  /**
   * 时间戳
   */
  private Long timestamp = System.currentTimeMillis();
}
