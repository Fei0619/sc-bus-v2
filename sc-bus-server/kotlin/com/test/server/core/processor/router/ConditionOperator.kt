package com.test.server.core.processor.router

/**
 * 条件运算符枚举
 * @author 费世程
 * @date 2020/8/14 10:31
 */
interface ConditionOperator {

  companion object {
    /**
     * 大于
     */
    var GRATER_THAN = '>'
    /**
     * 小于
     */
    var LESS_THAN = '<'
    /**
     * 等于
     */
    var EQUALS = '='
    /**
     * 在列表
     */
    var IN = '^'
  }

}