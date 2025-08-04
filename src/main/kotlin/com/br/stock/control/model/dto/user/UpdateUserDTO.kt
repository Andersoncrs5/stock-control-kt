package com.br.stock.control.model.dto.user

data class UpdateUserDTO(
    var name: String?,
    var passwordHash: String,
    var fullName: String?,
)
