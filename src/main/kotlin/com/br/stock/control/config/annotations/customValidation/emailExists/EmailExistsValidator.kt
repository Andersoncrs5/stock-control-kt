package com.br.stock.control.config.annotations.customValidation.emailExists

import com.br.stock.control.util.facades.FacadeRepository
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.stereotype.Component

@Component
class EmailExistsValidator(
    private val facadesRepository: FacadeRepository
): ConstraintValidator<EmailExists, String> {
    override fun isValid(email: String?, ctx: ConstraintValidatorContext?): Boolean {
        if (email.isNullOrBlank()) return false

        return !this.facadesRepository.userRepository.existsByEmail(email)
    }

}