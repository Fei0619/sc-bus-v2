package com.test.common.validator

import com.test.common.validator.constraint.IdentificationValidateConstraint
import javax.validation.Constraint

/**
 * @author 费世程
 * @date 2020/7/30 11:21
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
@Constraint(validatedBy = [IdentificationValidateConstraint::class])
annotation class CheckIdentification(
    val message: String = "身份证号不合法！"
)