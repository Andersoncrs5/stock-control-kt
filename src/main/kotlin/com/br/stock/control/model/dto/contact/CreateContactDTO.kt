package com.br.stock.control.model.dto.contact

data class CreateContactDTO(
    var secondaryEmail: String? = null,
    var phone: String? = null,
    var secondaryPhone: String? = null,
)
