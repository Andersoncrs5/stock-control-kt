package com.br.stock.control.config.annotations.customValidation.wareHouseNameExists

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [WareHouseNameExistsValidator::class])
annotation class WareHouseNameExists(
    val message: String = "Warehouse name exists",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

