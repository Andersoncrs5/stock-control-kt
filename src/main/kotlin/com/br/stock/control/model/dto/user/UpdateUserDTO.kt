package com.br.stock.control.model.dto.user

import com.br.stock.control.config.annotations.customValidation.cleanString.CleanString
import com.br.stock.control.config.annotations.customValidation.existsName.UserNameExists
import jakarta.validation.constraints.*

data class UpdateUserDTO(
    @field:NotBlank(message = "The 'name' field is required")
    @field:Size(min = 6, max = 50, message = "The 'name' field must be between 6 and 50 characters")
    @field:UserNameExists(message = "This username already exists")
    var name: String,

    @field:NotBlank(message = "The 'password' field is required")
    @field:Size(min = 6, max = 80, message = "The 'password' field must be between 6 and 80 characters")
    @field:CleanString
    var passwordHash: String,

    @field:NotBlank(message = "The 'fullName' field is required")
    @field:Size(min = 6, max = 150, message = "The 'fullName' field must be between 6 and 150 characters")
    var fullName: String,
)