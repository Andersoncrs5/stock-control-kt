package com.br.stock.control.model.dto.stock

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

class CreateStockDTO(
    @field:NotBlank(message = "The productId is required")
    var productId: String = "",

    @field:Min(value = 1, message = "The quantity must be at least 1")
    var quantity: Long = 0,

    @field:NotBlank(message = "The responsible is required")
    var responsibleUserId: String = "",

    @field:NotBlank(message = "The warehouse is required")
    var warehouseId: String = "",
)