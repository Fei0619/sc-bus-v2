package com.test.common.message;


import lombok.Data;

/**
 * @author 费世程
 * @date 2020/8/14 13:51
 */
@Data
public class CallbackMessage<T> {
  /**
   * 事件id
   * com.test.common.message.EventMessage.eventId
   */
  private String eventId;

  /**
   * 事件主题
   * com.test.common.message.EventMessage.topic
   */
  private String topic;

  /**
   * 消费方服务编码
   */
  private String consumeServiceCode;

  /**
   * 是否执行成功
   */
  private Boolean consumeSuccess = true;

  /**
   * 成功/失败描述信息
   */
  private String consumeMessage = "";

  /**
   * 响应内容
   * 消费方处理完[EventMessage]后的响应内容
   */
  private T payload;
}
