package com.test.common.validator

import com.test.common.validator.constraint.EmailValidateConstraint
import javax.validation.Constraint

/**
 * @author 费世程
 * @date 2020/7/30 11:02
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
@Constraint(validatedBy = [EmailValidateConstraint::class])
annotation class CheckEmail(
    val message: String = "邮箱不合法！"
)