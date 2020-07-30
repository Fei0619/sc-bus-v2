package com.test.common.date;

/**
 * @author 宋志宗
 * @date 2020/7/6
 */
public interface DatePattern {
  /**
   * 2020-12-12
   */
  String yyyy_MM_dd = "yyyy-MM-dd";
  /**
   * 2020-12-12 19
   */
  String yyyy_MM_dd_HH = "yyyy-MM-dd HH";
  /**
   * 2020-12-12 19:21
   */
  String yyyy_MM_dd_HH_mm = "yyyy-MM-dd HH:mm";
  /**
   * 2020-12-12 19:21:56
   */
  String yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
  /**
   * 2020-12-12 19:21:56.555
   */
  String yyyy_MM_dd_HH_mm_ss_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
  /**
   * 12-12 19:21:56
   */
  String MM_dd_HH_mm_ss = "MM-dd HH:mm:ss";
  /**
   * 12-12 19
   */
  String MM_dd_HH = "MM-dd HH";
  /**
   * 19:21:56
   */
  String HH_mm_ss = "HH:mm:ss";
  /**
   * 19:21
   */
  String HH_mm = "HH:mm";
}
