package com.br.stock.control.model.dto.category

import com.br.stock.control.config.annotations.customValidation.nameCategoryExists.UniqueCategoryName
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class CreateCategoryDTO (

    @NotBlank(message = "The field name is required")
    @Size(min = 4, max = 150, message = "The field name have max size of 150 and the min of 4")
    @UniqueCategoryName
    var name: String = "",

    @Size(max = 500, message = "The field description have max size of 500")
    var description: String? = "",
)