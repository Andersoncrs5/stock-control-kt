package com.br.stock.control.model.dto.product

import com.br.stock.control.model.enum.UnitOfMeasureEnum
import java.math.BigDecimal

data class CreateProductDTO(
    var name: String,
    var description: String,
    var sku: String,
    var barcode: String,
    var unitOfMeasure: UnitOfMeasureEnum,
    var price: BigDecimal,
    var cost: BigDecimal,
    var imageUrl: String,
    var minStockLevel: Int,
    var maxStockLevel: Int
)
