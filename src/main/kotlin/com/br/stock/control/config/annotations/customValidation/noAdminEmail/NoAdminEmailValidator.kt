package com.br.stock.control.config.annotations.customValidation.noAdminEmail

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.stereotype.Component

@Component
class NoAdminEmailValidator : ConstraintValidator<NoAdminEmail, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        if (value.isNullOrBlank()) return true
        val cleanEmail = value.trim().lowercase()
        return cleanEmail != "admin@gmail.com" && !cleanEmail.contains("admin")
    }
}
