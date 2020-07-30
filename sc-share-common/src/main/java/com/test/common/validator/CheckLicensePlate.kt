package com.test.common.validator

import javax.validation.Constraint

/**
 * @author 费世程
 * @date 2020/7/30 11:26
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
@Constraint(validatedBy = [LicensePlateValidicateConstraint::class])
annotation class CheckLicensePlate(
    val message: String = "车牌号不合法！"
)