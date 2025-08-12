package com.br.stock.control.model.dto.category

import com.br.stock.control.model.enum.TypeAddressEnum

class UpdateCategoryDTO(
    var street: String,
    var number: String? = null,
    var complement: String? = null,
    var neighborhood: String,
    var city: String,
    var state: String,
    var zipCode: String,
    var country: String,
    var referencePoint: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var isActive: Boolean = true,
    var type: TypeAddressEnum
) {

}