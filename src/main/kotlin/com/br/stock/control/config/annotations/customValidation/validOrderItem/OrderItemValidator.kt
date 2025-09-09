package com.br.stock.control.config.annotations.customValidation.validOrderItem

import com.br.stock.control.model.dto.purchaseOrderItem.CreateOrderItemDTO
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class OrderItemValidator : ConstraintValidator<OrderItem, CreateOrderItemDTO> {
    override fun isValid(value: CreateOrderItemDTO?, context: ConstraintValidatorContext): Boolean {
        if (value == null) return true

        val expected = value.expectedQuantity
        val received = value.receivedQuantity
        val backOrdered = value.backOrderedQuantity
        val quantity = value.quantity

        if (expected < quantity) {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate("Expected quantity must be >= requested quantity")
                .addPropertyNode("expectedQuantity")
                .addConstraintViolation()
            return false
        }

        if (received > expected) {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate("Received quantity cannot exceed expected quantity")
                .addPropertyNode("receivedQuantity")
                .addConstraintViolation()
            return false
        }

        if (received + backOrdered != expected) {
            context.disableDefaultConstraintViolation()
            context.buildConstraintViolationWithTemplate("BackOrdered + Received must equal Expected quantity")
                .addConstraintViolation()
            return false
        }

        return true
    }
}
