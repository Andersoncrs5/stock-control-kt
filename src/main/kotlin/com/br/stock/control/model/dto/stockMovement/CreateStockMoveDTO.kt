package com.br.stock.control.model.dto.stockMovement

import com.br.stock.control.model.enum.MovementTypeEnum

data class CreateStockMoveDTO(
    var stockId: String = "",
    var productId: String = "",
    var movementType: MovementTypeEnum? = null,
    var quantity: Long = 0,
    var reason: String? = null,
    var responsibleUserId: String = "",
    var notes: String? = null,
)
