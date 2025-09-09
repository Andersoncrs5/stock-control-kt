package com.br.stock.control.model.dto.purchaseOrder

import com.br.stock.control.model.enum.CurrencyEnum
import jakarta.validation.constraints.*
import java.time.LocalDate

data class CreateOrderDTO(
    @field:NotBlank(message = "The supplierId is required")
    var supplierId: String = "",

    @field:Future(message = "The expectedDeliveryDate must be in the future")
    @NotNull
    var expectedDeliveryDate: LocalDate? = null,

    @field:NotNull(message = "The currency is required")
    @field:Pattern(
        regexp = "^(?!NONE$).*",
        message = "The currency cannot be NONE"
    )
    var currency: CurrencyEnum = CurrencyEnum.NONE,

    @field:Size(max = 1000, message = "The notes field must have at most 1000 characters")
    var notes: String? = null,
)