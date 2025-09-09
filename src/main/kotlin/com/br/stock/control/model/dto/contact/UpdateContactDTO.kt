package com.br.stock.control.model.dto.contact

import com.br.stock.control.config.annotations.customValidation.lowerCaseString.LowerCaseString
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UpdateContactDTO(
    @Email(message = "Email invalid")
    @NotBlank(message = "Email is required")
    @LowerCaseString
    @Size(min = 8, max = 150, message = "The field secondary email have max size of 150 and the min of 4")
    var secondaryEmail: String? = null,

    @NotBlank(message = "Phone is required")
    @Size(min = 6, max = 50, message = "The field phone have max size of 50 and the min of 4")
    var phone: String? = null,

    @Size(max = 50, message = "The field secondary phone have max size of 50 and the min of 4")
    var secondaryPhone: String? = null,
)
