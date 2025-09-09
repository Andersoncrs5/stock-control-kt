package com.br.stock.control.model.dto.warehouse

import com.br.stock.control.config.annotations.customValidation.wareHouseNameExists.WareHouseNameExists
import com.br.stock.control.model.enum.WareHouseEnum
import jakarta.validation.constraints.*

data class UpdateWareDTO(
    @field:NotBlank(message = "The 'name' field is required")
    @field:Size(min = 8, max = 100, message = "The 'name' field must be between 8 and 100 characters")
    @field:WareHouseNameExists
    var name: String = "",

    @field:Size(max = 1000, message = "The 'description' field can have a maximum of 1000 characters")
    var description: String = "",

    var addressId: String = "",

    @field:NotBlank(message = "The responsible User field is required")
    var responsibleUserId: String = "",

    @field:NotNull(message = "The amount field is required")
    @field:Max(99_999_999_999, message = "The amount field cannot exceed 99,999,999,999")
    var amount: Long = 0L,

    @field:DecimalMax(value = "9999999.9999", message = "The capacityCubicMeters field must not exceed 9,999,999.9999")
    @field:PositiveOrZero(message = "The capacityCubicMeters field must be zero or positive")
    var capacityCubicMeters: Double = 0.0,

    @field:NotNull(message = "The type field is required")
    @field:Pattern(
        regexp = "^(?!NONE$).*",
        message = "The type field cannot be NONE"
    )
    var type: WareHouseEnum = WareHouseEnum.NONE
)