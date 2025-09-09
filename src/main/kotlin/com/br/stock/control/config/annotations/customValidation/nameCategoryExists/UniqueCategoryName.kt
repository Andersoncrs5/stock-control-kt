package com.br.stock.control.config.annotations.customValidation.nameCategoryExists

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [UniqueCategoryNameValidator::class])
annotation class UniqueCategoryName(
    val message: String = "The name category already exists",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)