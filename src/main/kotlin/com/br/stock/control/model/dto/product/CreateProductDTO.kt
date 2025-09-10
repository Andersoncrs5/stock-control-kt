package com.br.stock.control.model.dto.product

import com.br.stock.control.config.annotations.customValidation.uniqueProductBarcode.UniqueProductBarcode
import com.br.stock.control.config.annotations.customValidation.uniqueProductSku.UniqueProductSku
import com.br.stock.control.model.enum.UnitOfMeasureEnum
import jakarta.validation.constraints.*
import java.math.BigDecimal

data class CreateProductDTO(

    @field:NotBlank(message = "The field name is required")
    @field:Size(min = 2, max = 150, message = "The field name must be between 2 and 150 characters")
    var name: String = "",

    @field:NotBlank(message = "The field description is required")
    @field:Size(min = 5, max = 500, message = "The field description must be between 5 and 500 characters")
    var description: String = "",

    @field:NotBlank(message = "The field sku is required")
    @field:Size(min = 3, max = 150, message = "The field sku must be between 3 and 150 characters")
    @field:UniqueProductSku
    var sku: String = "",

    @field:NotBlank(message = "The field barcode is required")
    @field:UniqueProductBarcode
    var barcode: String = "",

    @field:NotNull(message = "The field unitOfMeasure is required")
    var unitOfMeasure: UnitOfMeasureEnum = UnitOfMeasureEnum.UNIT,

    @field:DecimalMin(value = "0.0", inclusive = false, message = "The field price must be greater than 0")
    @field:Digits(integer = 15, fraction = 2, message = "The field price must be a valid monetary value")
    var price: BigDecimal = BigDecimal.ZERO,

    @field:DecimalMin(value = "0.0", inclusive = true, message = "The field cost must not be negative")
    @field:Digits(integer = 15, fraction = 2, message = "The field cost must be a valid monetary value")
    var cost: BigDecimal = BigDecimal.ZERO,

    @field:Size(max = 500, message = "The field imageUrl must be at most 500 characters")
    var imageUrl: String = "",

    @field:Min(value = 0, message = "The field minStockLevel cannot be negative")
    var minStockLevel: Int = 0,

    @field:Max(value = 9999999, message = "The field maxStockLevel must be less than or equal to 9,999,999")
    var maxStockLevel: Int = 0
)
