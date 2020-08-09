package com.test.server.context

import com.test.server.mongo.document.ServiceDetails

/**
 * @author 费世程
 * @date 2020/8/10 0:34
 */
class AutoSubscribeContext {

  /**
   * 是否成功
   */
  var success = true
  /**
   * 异常信息
   */
  var message = ""
  /**
   * 处理状态
   */
  var status = Status.None
  /**
   * 需要保存的服务详情
   */
  var serviceDetails: ServiceDetails? = null

  enum class Status {
    /**
     * 无任何变化
     */
    None,
    /**
     * 新增
     */
    Add,
    /**
     * 只更新了基本信息
     */
    UpdateBasicOnly,
    /**
     * 更新订阅关系
     */
    UpdateSubscribe,
    /**
     * 服务码冲突
     */
    serviceCodeConfilct;
  }

}