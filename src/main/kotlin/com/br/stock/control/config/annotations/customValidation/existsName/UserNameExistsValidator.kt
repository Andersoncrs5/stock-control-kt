package com.br.stock.control.config.annotations.customValidation.existsName

import com.br.stock.control.util.facades.FacadeRepository
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.stereotype.Component

@Component
class UserNameExistsValidator(
    private val facadeRepository: FacadeRepository
) : ConstraintValidator<UserNameExists, String> {
    override fun isValid(username: String?, context: ConstraintValidatorContext?): Boolean {
        if (username.isNullOrBlank()) return false
        return !facadeRepository.userRepository.existsByName(username)
    }
}
