package com.br.stock.control.config.annotations.customValidation.nameCategoryExists

import com.br.stock.control.util.facades.FacadeRepository
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.stereotype.Component

@Component
class UniqueCategoryNameValidator(
    private val facadeRepository: FacadeRepository
): ConstraintValidator<UniqueCategoryName, String> {
    override fun isValid(name: String?, ctx: ConstraintValidatorContext?): Boolean {
        if (name.isNullOrBlank()) {
            return true
        }

        return !this.facadeRepository.categoryRepository.existsByName(name)
    }
}