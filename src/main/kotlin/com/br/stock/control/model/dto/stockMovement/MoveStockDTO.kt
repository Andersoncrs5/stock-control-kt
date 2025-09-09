package com.br.stock.control.model.dto.stockMovement

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

data class MoveStockDTO(
    @field:NotBlank(message = "The stock Origin is required")
    var stockIdOrigin: String,

    @field:NotBlank(message = "The stock Destination is required")
    var stockIdDestination: String,

    @field:Positive(message = "The quantity must be greater than 0")
    var quantity: Long
)