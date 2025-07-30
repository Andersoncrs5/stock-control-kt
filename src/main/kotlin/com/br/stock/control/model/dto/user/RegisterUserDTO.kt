package com.br.stock.control.model.dto.user

data class RegisterUserDTO(
    var name: String,
    var email: String,
    var passwordHash: String,
    var fullName: String
)