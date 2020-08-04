package com.test.api.constant;

/**
 * @author 费世程
 * @date 2020/8/2 17:31
 */
public interface BusUrl {

  /**
   * 客户端接收时间推送的接口地址
   */
  String CLIENT_EVENT_RECEIVE_URL = "/bus/receive/event";
  /**
   * 客户端接收推送回调的接口地址
   */
  String CLIENT_CALLBACK_RECEIVE_URL = "bus/receive/callback";

}
