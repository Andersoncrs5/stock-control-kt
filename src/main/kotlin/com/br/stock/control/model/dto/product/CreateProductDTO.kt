package com.br.stock.control.model.dto.product

import com.br.stock.control.model.enum.UnitOfMeasureEnum
import java.math.BigDecimal

data class CreateProductDTO(
    var name: String = "",
    var description: String = "",
    var sku: String = "",
    var barcode: String = "",
    var unitOfMeasure: UnitOfMeasureEnum? = null,
    var price: BigDecimal? = null,
    var cost: BigDecimal? = null,
    var imageUrl: String = "",
    var minStockLevel: Int = 0,
    var maxStockLevel: Int = 0
)
