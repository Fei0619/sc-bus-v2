package com.test.server.core.processor.router

import com.test.common.json.JsonUtils
import com.test.common.message.EventHeaders
import org.slf4j.LoggerFactory

/**
 * 条件匹配器
 *
 * @author 费世程
 * @date 2020/8/14 9:53
 */
class ConditionMatcher {

  companion object {

    private val log = LoggerFactory.getLogger(ConditionMatcher::class.java)

    /**
     * 条件匹配
     * @param conditions List<Set<String>> 订阅条件
     * @param headers EventHeaders 推送条件
     * @return Boolean 条件是否匹配
     * @author 费世程
     * @date 2020/8/14 9:58
     */
    fun match(conditions: List<Set<String>>?, headers: EventHeaders?): Boolean {
      if (conditions.isNullOrEmpty()) {
        return true
      }
      if (headers.isNullOrEmpty()) {
        return false
      }
      for (condition in conditions) {
        if (matchConditions(condition, headers)) return true
      }
      return false
    }

    //------------------------------------------ 私有方法 -------------------------------------------//

    /**
     * operator: > < = ^(在列表)
     */
    private fun matchConditions(conditionSet: Set<String>, headers: EventHeaders): Boolean {
      if (conditionSet.isNullOrEmpty()) return true
      for (condition in conditionSet) {
        var operator: Char = '0'
        var index = -1
        val chars = condition.toCharArray()
        for (i in chars.indices) {
          if (chars[i] == ConditionOperator.GRATER_THAN ||
              chars[i] == ConditionOperator.LESS_THAN ||
              chars[i] == ConditionOperator.EQUALS ||
              chars[i] == ConditionOperator.IN) {
            operator = chars[i]
            index = i
            break
          }
        }
        if (index < 1) {
          log.debug("条件不匹配，缺少运算符 -> conditionSet=$conditionSet")
        }
        val key = condition.substring(0, index)
        val headValues = headers[key]
        if (headValues.isNullOrEmpty()) return false
        val value = condition.substring(index + 1)

        when (operator) {
          ConditionOperator.GRATER_THAN -> { //大于
            try {
              val valueLong = value.toLong()
              for (headValue in headValues) {
                val headValueLong = headValue.toLong()
                if (headValueLong < valueLong) return false
              }
            } catch (e: NumberFormatException) {
              log.warn("GEATER_THAN条件比较时出现NumberFormatException异常：conditionValue=${value},headValues=${JsonUtils.toJsonString(headValues)}")
            }
          }
          ConditionOperator.LESS_THAN -> { //小于
            try {
              val valueLong = value.toLong()
              for (headValue in headValues) {
                val headValueLong = headValue.toLong()
                if (headValueLong > valueLong) return false
              }
            } catch (e: NumberFormatException) {
              log.warn("LESS_THAN条件比较时出现NumberFormatException异常：conditionValue=${value},headValues=${JsonUtils.toJsonString(headValues)}")
            }
          }
          ConditionOperator.EQUALS -> {
            if (!headValues.contains(value)) {
              return false
            }
          }
          ConditionOperator.IN -> {
            val valueIn = value.split(",")
            var flag = false
            for (it in valueIn) {
              if (headValues.contains(it)) {
                flag = true
                break
              }
            }
            return flag
          }
        }
      }
      return true
    }

  }

}