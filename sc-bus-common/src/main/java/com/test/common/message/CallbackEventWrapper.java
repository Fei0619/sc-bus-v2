package com.test.common.message;

import org.jetbrains.annotations.NotNull;

/**
 * @author 费世程
 * @date 2020/8/13 14:02
 */
public class CallbackEventWrapper {

  @NotNull
  private String topic;
  @NotNull
  private String callbackMessage;

  CallbackEventWrapper(@NotNull String topic, @NotNull String callbackMessage) {
    this.topic = topic;
    this.callbackMessage = callbackMessage;
  }

  @NotNull
  public String getTopic() {
    return topic;
  }

  public void setTopic(@NotNull String topic) {
    this.topic = topic;
  }

  @NotNull
  public String getCallbackMessage() {
    return callbackMessage;
  }

  public void setCallbackMessage(@NotNull String callbackMessage) {
    this.callbackMessage = callbackMessage;
  }
}
