package com.br.stock.control.config.annotations.customValidation.cleanString

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class CleanStringValidator : ConstraintValidator<CleanString, String> {

    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        if (value == null) return true

        val cleaned = value.trim().replace("\\s+".toRegex(), " ")

        return cleaned.isNotEmpty()
    }
}
