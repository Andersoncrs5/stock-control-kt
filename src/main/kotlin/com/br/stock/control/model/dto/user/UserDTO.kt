package com.br.stock.control.model.dto.user

import java.time.LocalDateTime

class UserDTO(
    var id: String = "",
    var name: String= "",
    var email: String= "",
    var fullName: String= "",
    var addressId: String? = "",
    var createdAt: LocalDateTime? = null
) {
}