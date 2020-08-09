package com.test.common.result

import com.fasterxml.jackson.annotation.JsonInclude
import org.jetbrains.annotations.Nullable


/**
 * @author 费世程
 * @date 2020/8/5 0:57
 */
class Res<T> private constructor() {

  private constructor(code: Int, message: String, data: T?, success: Boolean) : this() {
    this.code = code
    this.message = message
    this.data = data
    this.success = success
  }

  //--------------------------------------- 自定义参数 ----------------------------------------------//
  /** 状态码 */
  private var code: Int? = null
  /** 提示信息 */
  private var message: String? = null
  /**
   * 响应数据
   * @JsonInclude - 实体类与json互转的时候 属性值为null的不参与序列化
   */
  @Nullable
  @JsonInclude(value = JsonInclude.Include.NON_NULL)
  private var data: T? = null
  /** 是否成功 */
  private var success: Boolean? = null

  //--------------------------------------- 自定义参数 ----------------------------------------------//

  companion object {

    fun <T> create(): Res<T> {
      return Res()
    }

    fun <T> success(): Res<T> {
      return Res(CommonResMsg.SUCCESS.code(), CommonResMsg.SUCCESS.message(), null, true)
    }

    fun <T> success(message: String): Res<T> {
      return Res(CommonResMsg.SUCCESS.code(), message, null, true)
    }

    fun <T> error(): Res<T> {
      return Res(CommonResMsg.BAD_REQUEST.code(), CommonResMsg.BAD_REQUEST.message(), null, false)
    }

    fun <T> error(message: String): Res<T> {
      return Res(CommonResMsg.BAD_REQUEST.code(), message, null, false)
    }

    fun <T> data(data: T): Res<T> {
      return Res(CommonResMsg.SUCCESS.code(), CommonResMsg.SUCCESS.message(), data, false)
    }

    fun <T> data(data: T, message: String): Res<T> {
      return Res(CommonResMsg.SUCCESS.code(), message, data, false)
    }

  }

}