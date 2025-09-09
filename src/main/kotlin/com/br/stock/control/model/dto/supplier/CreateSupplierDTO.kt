package com.br.stock.control.model.dto.supplier

import com.br.stock.control.model.enum.SupplierTypeEnum
import jakarta.validation.constraints.*

data class CreateSupplierDTO(
    @field:NotBlank(message = "The CNPJ is required")
    var cnpj: String = "",

    @field:NotBlank(message = "The enterprise name is required")
    var nameEnterprise: String = "",

    @field:Size(max = 1000, message = "The notes can have max 1000 characters")
    var notes: String = "",

    @field:NotNull(message = "The supplier type is required")
    @field:Pattern(
        regexp = "^(?!NONE$).*",
        message = "The type cannot be NONE"
    )
    var type: SupplierTypeEnum = SupplierTypeEnum.NONE,

    @field:Min(0, message = "The rating must be at least 0")
    @field:Max(10, message = "The rating cannot be greater than 10")
    var rating: Int = 0,

    var categoriesId: MutableList<String> = mutableListOf(),

    @field:NotBlank(message = "The creator is required")
    var createdBy: String = ""
)