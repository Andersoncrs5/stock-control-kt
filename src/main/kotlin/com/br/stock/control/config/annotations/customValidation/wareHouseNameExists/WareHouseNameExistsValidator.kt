package com.br.stock.control.config.annotations.customValidation.wareHouseNameExists

import com.br.stock.control.util.facades.FacadeRepository
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.stereotype.Component

@Component
class WareHouseNameExistsValidator(
    private val facadeRepository: FacadeRepository
) : ConstraintValidator<WareHouseNameExists, String> {
    override fun isValid(name: String?, context: ConstraintValidatorContext?): Boolean {
        if (name.isNullOrBlank()) return false
        return !facadeRepository.wareHouseRepository.existsByName(name)
    }
}