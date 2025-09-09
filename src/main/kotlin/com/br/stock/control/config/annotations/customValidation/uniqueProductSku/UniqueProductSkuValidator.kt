package com.br.stock.control.config.annotations.customValidation.uniqueProductSku

import com.br.stock.control.util.facades.FacadeRepository
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.stereotype.Component

@Component
class UniqueProductSkuValidator(
    private val facadeRepository: FacadeRepository
): ConstraintValidator<UniqueProductSku, String> {
    override fun isValid(sku: String?, p1: ConstraintValidatorContext?): Boolean {
        if (sku.isNullOrBlank()) return false
        return !facadeRepository.productRepository.existsBySku(sku)
    }
}