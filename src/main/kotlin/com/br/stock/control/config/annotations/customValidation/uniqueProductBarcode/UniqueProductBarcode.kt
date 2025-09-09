package com.br.stock.control.config.annotations.customValidation.uniqueProductBarcode

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [UniqueProductBarcodeValidator::class])
annotation class UniqueProductBarcode(
    val message: String = "The barcode already exists",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
