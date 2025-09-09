package com.br.stock.control.model.dto.user

import com.br.stock.control.config.annotations.customValidation.cleanString.CleanString
import com.br.stock.control.config.annotations.customValidation.emailExists.EmailExists
import com.br.stock.control.config.annotations.customValidation.existsName.UserNameExists
import com.br.stock.control.config.annotations.customValidation.lowerCaseString.LowerCaseString
import com.br.stock.control.config.annotations.customValidation.noAdminEmail.NoAdminEmail
import jakarta.validation.constraints.*

data class RegisterUserDTO(
    @field:NotBlank(message = "The 'name' field is required")
    @field:Size(min = 6, max = 50, message = "The 'name' field must be between 6 and 50 characters")
    @field:UserNameExists(message = "This username already exists")
    var name: String,

    @field:NotBlank(message = "The 'email' field is required")
    @field:Size(min = 8, max = 150, message = "The 'email' field must be between 8 and 150 characters")
    @field:Email(message = "The 'email' field must be a valid email address")
    @field:CleanString
    @field:EmailExists(message = "This email is already registered")
    @field:LowerCaseString
    @field:NoAdminEmail
    var email: String,

    @field:NotBlank(message = "The 'password' field is required")
    @field:Size(min = 6, max = 80, message = "The 'password' field must be between 6 and 80 characters")
    @field:CleanString
    var passwordHash: String,

    @field:NotBlank(message = "The 'fullName' field is required")
    @field:Size(min = 6, max = 150, message = "The 'fullName' field must be between 6 and 150 characters")
    val fullName: String
)