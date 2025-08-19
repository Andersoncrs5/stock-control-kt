package com.br.stock.control.model.dto.stock

class CreateStockDTO(
    var productId: String = "",
    var quantity: Long = 0,
    var responsibleUserId: String = "",
    var warehouseId: String = "",
)