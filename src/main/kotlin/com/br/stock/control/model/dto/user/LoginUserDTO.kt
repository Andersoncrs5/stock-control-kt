package com.br.stock.control.model.dto.user

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LoginUserDTO(
    @field:NotBlank(message = "The username is required")
    @field:Size(min = 6, max = 50, message = "Username must be between 6 and 50 characters")
    val name: String,

    @field:NotBlank(message = "The password is required")
    @field:Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    val password: String
)
