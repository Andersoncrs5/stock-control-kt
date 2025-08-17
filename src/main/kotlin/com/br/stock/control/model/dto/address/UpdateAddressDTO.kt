package com.br.stock.control.model.dto.address

data class UpdateAddressDTO(
    var street: String = "",
    var number: String = "",
    var complement: String = "",
    var neighborhood: String = "",
    var city: String = "",
    var state: String = "",
    var zipCode: String = "",
    var country: String = "",
    var referencePoint: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var isActive: Boolean = true
)
