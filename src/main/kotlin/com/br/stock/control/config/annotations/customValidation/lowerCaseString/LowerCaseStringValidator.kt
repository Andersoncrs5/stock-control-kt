package com.br.stock.control.config.annotations.customValidation.lowerCaseString

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class LowerCaseStringValidator : ConstraintValidator<LowerCaseString, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        if (value.isNullOrBlank()) return true

        return value == value.lowercase()
    }
}
