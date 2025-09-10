package com.br.stock.control.model.dto.purchaseOrder

import com.br.stock.control.model.enum.CurrencyEnum
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class UpdateOrderDTO(
    @field:NotBlank(message = "The supplierId is required")
    var supplierId: String = "",

    @field:Future(message = "The expectedDeliveryDate must be in the future")
    var expectedDeliveryDate: LocalDate? = null,

    @field:NotNull(message = "The currency is required")
    var currency: CurrencyEnum = CurrencyEnum.USD,

    @field:Size(max = 1000, message = "The notes field must have at most 1000 characters")
    var notes: String? = null,
)