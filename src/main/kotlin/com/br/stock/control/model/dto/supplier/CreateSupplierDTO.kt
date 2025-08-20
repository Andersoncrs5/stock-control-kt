package com.br.stock.control.model.dto.supplier

import com.br.stock.control.model.enum.SupplierTypeEnum

data class CreateSupplierDTO(
    var cnpj: String = "",
    var nameEnterprise: String = "",
    var notes: String = "",
    var type: SupplierTypeEnum = SupplierTypeEnum.NONE,
    var rating: Int = 0,
    var categoriesId: MutableList<String> = mutableListOf(),
    var createdBy: String = "",
)