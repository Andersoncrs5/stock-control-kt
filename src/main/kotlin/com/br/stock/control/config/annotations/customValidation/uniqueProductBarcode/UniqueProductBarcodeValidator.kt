package com.br.stock.control.config.annotations.customValidation.uniqueProductBarcode

import com.br.stock.control.util.facades.FacadeRepository
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.stereotype.Component

@Component
class UniqueProductBarcodeValidator(
    private val facadeRepository: FacadeRepository
): ConstraintValidator<UniqueProductBarcode, String> {
    override fun isValid(barcode: String?, p1: ConstraintValidatorContext?): Boolean {
        if (barcode.isNullOrBlank()) return false
        return !facadeRepository.productRepository.existsByBarcode(barcode)
    }
}