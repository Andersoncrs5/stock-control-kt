package com.br.stock.control.model.dto.stock

class UpdateStockDTO(
    var quantity: Int = 0,
    var responsibleUserId: String = "",
    var warehouseId: String = "",
)