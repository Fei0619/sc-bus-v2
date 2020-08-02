package com.test.common.validator

import com.test.common.validator.constraint.MobileValidateConstraint
import javax.validation.Constraint

/**
 * @author 费世程
 * @date 2020/7/30 11:15
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
@Constraint(validatedBy = [MobileValidateConstraint::class])
annotation class CheckMobile(
    val message: String = "手机号不合法!"
)