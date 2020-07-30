package com.test.common.validator

import java.util.regex.Pattern
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

/**
 * @author 费世程
 * @date 2020/7/30 11:10
 */
class EmailValidateConstraint : ConstraintValidator<CheckEmail, String> {

  companion object {
    private const val regex = "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$"
    private val pattern = Pattern.compile(regex)
  }

  override fun isValid(email: String, p1: ConstraintValidatorContext): Boolean {
    return pattern.matcher(email).matches()
  }

}