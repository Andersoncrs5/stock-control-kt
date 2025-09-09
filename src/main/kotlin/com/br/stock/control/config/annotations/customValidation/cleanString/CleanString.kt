package com.br.stock.control.config.annotations.customValidation.cleanString

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [CleanStringValidator::class])
annotation class CleanString(
    val message: String = "Invalid string",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
