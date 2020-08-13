package com.test.common.message;

import org.jetbrains.annotations.NotNull;

/**
 * @author 费世程
 * @date 2020/8/13 13:57
 */
public class EventWrapper {

  @NotNull
  private String topic;
  @NotNull
  private String eventMessage;

  EventWrapper(@NotNull String topic, @NotNull String eventMessage) {
    this.topic = topic;
    this.eventMessage = eventMessage;
  }

  @NotNull
  public String getTopic() {
    return topic;
  }

  public void setTopic(@NotNull String topic) {
    this.topic = topic;
  }

  @NotNull
  public String getEventMessage() {
    return eventMessage;
  }

  public void setEventMessage(@NotNull String eventMessage) {
    this.eventMessage = eventMessage;
  }
}
