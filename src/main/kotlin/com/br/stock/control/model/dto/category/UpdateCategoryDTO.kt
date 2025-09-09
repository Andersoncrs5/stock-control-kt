package com.br.stock.control.model.dto.category

import jakarta.validation.constraints.*

class UpdateCategoryDTO(
    @NotBlank(message = "The field name is required")
    @Size(min = 4, max = 150, message = "The field name have max size of 150 and the min of 4")
    var name: String = "",

    @Size(max = 500, message = "The field description have max size of 500")
    var description: String? = "",
)