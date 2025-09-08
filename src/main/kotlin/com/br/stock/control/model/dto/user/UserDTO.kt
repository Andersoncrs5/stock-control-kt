package com.br.stock.control.model.dto.user

import java.time.LocalDate

class UserDTO(
    var id: String = "",
    var name: String= "",
    var email: String= "",
    var fullName: String= "",
    var createdAt: LocalDate? = null
)