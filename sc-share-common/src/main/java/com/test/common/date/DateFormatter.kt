package com.test.common.date

import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * @author 宋志宗
 * @date 2019/9/18
 */
object DateFormatter {
  private val map = ConcurrentHashMap<String, DateTimeFormatter>()
  /**
   * 通过[pattern]和[locale]获取[DateTimeFormatter]对象
   * @author 宋志宗
   * @date 2019/9/8
   */
  fun of(pattern: String = DatePattern.yyyy_MM_dd_HH_mm_ss,
         locale: Locale = Locale.SIMPLIFIED_CHINESE): DateTimeFormatter {
    val key = "$pattern:${locale.language}:${locale.country}"
    return map.computeIfAbsent(key) { DateTimeFormatter.ofPattern(pattern, locale) }
  }
}