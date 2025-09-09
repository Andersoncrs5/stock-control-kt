package com.br.stock.control.config.annotations.customValidation.existsName

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [UserNameExistsValidator::class])
annotation class UserNameExists(
    val message: String = "User name exists",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

