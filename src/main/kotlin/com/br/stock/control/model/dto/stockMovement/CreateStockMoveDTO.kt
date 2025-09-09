package com.br.stock.control.model.dto.stockMovement

import com.br.stock.control.model.enum.MovementTypeEnum
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

data class CreateStockMoveDTO(
    @field:NotBlank(message = "The stock is required")
    var stockId: String = "",

    @field:NotBlank(message = "The product is required")
    var productId: String = "",

    @field:NotNull(message = "The movement type is required")
    var movementType: MovementTypeEnum,

    @field:Positive(message = "The quantity must be greater than 0")
    var quantity: Long = 1,

    @field:Size(max = 600, message = "The field reason has a max size of 600")
    var reason: String? = null,

    @field:NotBlank(message = "The responsible user is required")
    var responsibleUserId: String = "",

    @field:Size(max = 600, message = "The field notes has a max size of 600")
    var notes: String? = null,
)
