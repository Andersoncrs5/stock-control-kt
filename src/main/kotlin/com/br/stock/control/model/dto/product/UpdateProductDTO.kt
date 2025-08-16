package com.br.stock.control.model.dto.product

import com.br.stock.control.model.enum.UnitOfMeasureEnum
import java.math.BigDecimal

data class UpdateProductDTO(
    var name: String = "",
    var description: String = "",
    var sku: String = "",
    var barcode: String = "",
    var unitOfMeasure: UnitOfMeasureEnum = UnitOfMeasureEnum.UNIT,
    var price: BigDecimal = BigDecimal.valueOf(0.0),
    var cost: BigDecimal = BigDecimal.valueOf(0.0),
    var imageUrl: String = "",
    var minStockLevel: Int = 0,
    var maxStockLevel: Int = 0,
    var locationSpecificStock: Map<String, Int> = mapOf(),
)