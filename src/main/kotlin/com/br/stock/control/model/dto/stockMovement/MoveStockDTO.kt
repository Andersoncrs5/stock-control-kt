package com.br.stock.control.model.dto.stockMovement

data class MoveStockDTO(
    var stockIdOrigin: String,
    var stockIdDestination: String,
    var quantity: Long
)