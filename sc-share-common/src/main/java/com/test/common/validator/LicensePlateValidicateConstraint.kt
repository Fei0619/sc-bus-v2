package com.test.common.validator

import java.util.regex.Pattern
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

/**
 * @author 费世程
 * @date 2020/7/30 11:28
 */
class LicensePlateValidicateConstraint : ConstraintValidator<CheckLicensePlate, String> {

  companion object {
    private const val regex = "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领A-Z][A-Z][警京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼]?[A-Z0-9]{4}[A-Z0-9挂学警港澳]$"
    private val pattern = Pattern.compile(regex)
  }

  override fun isValid(licensePlate: String, p1: ConstraintValidatorContext): Boolean {
    return pattern.matcher(licensePlate).matches()
  }
}