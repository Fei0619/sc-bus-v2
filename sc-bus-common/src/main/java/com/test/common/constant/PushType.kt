package com.test.common.constant

/**
 * @author 费世程
 * @date 2020/8/2 17:57
 */
enum class PushType {

  /**
   * 服务和bus在同一注册中心，通过负载均衡进行调用
   */
  LoadBalance,
  /**
   * 服务和bus不在同一注册中心，使用完整的访问地址
   */
  Host, ;

}