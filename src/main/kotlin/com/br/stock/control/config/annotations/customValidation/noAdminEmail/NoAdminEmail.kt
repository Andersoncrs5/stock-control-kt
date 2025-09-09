package com.br.stock.control.config.annotations.customValidation.noAdminEmail

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [NoAdminEmailValidator::class])
annotation class NoAdminEmail(
    val message: String = "The email cannot contain 'admin' or be 'admin@gmail.com'",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
