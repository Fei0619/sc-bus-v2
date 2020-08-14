package com.test.common.generate;

import com.test.common.message.EventMessage;
import com.test.common.message.EventPayload;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 事件生成器
 *
 * @author 费世程
 * @date 2020/8/13 15:13
 */
public class EventGenerator {

  //事件列表
  private List<EventMessage<?>> messages = new ArrayList<>();
  private Integer cursor = 0;

  private EventGenerator() {

  }

  public static EventGenerator create() {
    return new EventGenerator();
  }

  public static EventGenerator create(String topic, Object payload) {
    final EventGenerator generator = new EventGenerator();
    final EventMessage<Object> message = constructEventMessage(topic, payload);
    generator.messages.add(message);
    generator.cursor++;
    return generator;
  }

  public static EventGenerator create(EventPayload payload) {
    if (payload == null) {
      throw new NullPointerException("EventPayload is null~");
    }
    return create(payload.getTopic(), payload);
  }

  public EventGenerator then(String topic, Object payload) {
    final EventMessage<Object> message = constructEventMessage(topic, payload);
    this.messages.add(message);
    this.cursor++;
    return this;
  }

  public EventGenerator then(EventPayload payload) {
    if (payload == null) {
      throw new NullPointerException("EventPayload is null~");
    }
    return then(payload.getTopic(), payload);
  }

  public EventGenerator addHeader(String key, String value) {
    EventMessage message = this.messages.get(cursor);
    message.getHeaders().add(key, value);
    return this;
  }

  public EventGenerator addHeader(String key, String... values) {
    EventMessage message = this.messages.get(cursor);
    message.getHeaders().addAll(key, Arrays.asList(values));
    return this;
  }

  public EventGenerator addHeader(String key, Collection<String> values) {
    EventMessage message = this.messages.get(cursor);
    message.getHeaders().addAll(key, values);
    return this;
  }

  public EventGenerator idemponentKey(String key) {
    EventMessage message = this.messages.get(cursor);
    message.setIdempotentKey(key);
    return this;
  }

  public EventGenerator delay(Long delayMillis) {
    EventMessage message = this.messages.get(cursor);
    message.setDelayMillis(delayMillis);
    return this;
  }

  public EventGenerator callback(Boolean callback) {
    EventMessage message = this.messages.get(cursor);
    message.setCallback(callback);
    return this;
  }

  public List<EventMessage<?>> getMessages() {
    return messages;
  }

  //--------------------------------------------- 私有方法 ----------------------------------------------//

  private static EventMessage<Object> constructEventMessage(String topic, Object payload) {
    if (topic == null || payload == null || topic.isEmpty()) {
      throw new NullPointerException("topic or payload is null~");
    }
    final EventMessage<Object> message = new EventMessage<>();
    message.setTopic(topic);
    message.setPayload(payload);
    return message;
  }

}
