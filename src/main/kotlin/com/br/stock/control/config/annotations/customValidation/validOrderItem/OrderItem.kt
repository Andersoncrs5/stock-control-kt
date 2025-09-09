package com.br.stock.control.config.annotations.customValidation.validOrderItem

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [OrderItemValidator::class])
annotation class OrderItem(
    val message: String = "Invalid order item quantities",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
