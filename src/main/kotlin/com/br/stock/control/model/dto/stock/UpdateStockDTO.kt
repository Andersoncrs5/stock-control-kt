package com.br.stock.control.model.dto.stock

class UpdateStockDTO(
    var quantity: Long = 0,
    var responsibleUserId: String = "",
    var warehouseId: String = "",
)