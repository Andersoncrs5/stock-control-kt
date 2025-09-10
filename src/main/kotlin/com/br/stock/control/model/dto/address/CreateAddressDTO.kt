package com.br.stock.control.model.dto.address

import com.br.stock.control.config.annotations.customValidation.cleanString.CleanString
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateAddressDTO(

    @NotBlank(message = "The field street is required")
    @Size(min = 4, max = 150, message = "The field street have max size of 150 and the min of 4")
    var street: String = "",

    @CleanString
    @NotBlank(message = "The field number is required")
    @Size(min = 1, max = 500, message = "The field number have max size of 500 and the min of 1")
    var number: String = "",

    @Size(max = 500, message = "The field complement have max size of 500")
    var complement: String = "",

    @NotBlank(message = "The field neighborhood is required")
    @Size(min = 4, max = 200, message = "The field neighborhood have max size of 200 and min of 4")
    var neighborhood: String = "",

    @NotBlank(message = "The field city is required")
    @Size(min = 1, max = 300, message = "The field city have max size of 300 and min of 1")
    var city: String = "",

    @NotBlank(message = "The field state is required")
    @Size(min = 1, max = 200, message = "The field state have max size of 200 and min of 1")
    var state: String = "",

    @CleanString
    @Size(max = 200, message = "The field zipCode have max size of 200")
    var zipCode: String = "",

    @NotBlank(message = "The field country is required")
    @Size(min = 4, max = 250, message = "The field country have max size of 250 and min of 4")
    var country: String = "",

    @Size(max = 600, message = "The field referencePoint have max size of 600")
    var referencePoint: String = "",

    @DecimalMin(value = "-90.0", message = "Latitude must be greater or equal to -90.0")
    @DecimalMax(value = "90.0", message = "Latitude must be less or equal to 90.0")
    var latitude: Double = 0.0,

    @DecimalMin(value = "-180.0", message = "Longitude must be greater or equal to -180.0")
    @DecimalMax(value = "180.0", message = "Longitude must be less or equal to 180.0")
    var longitude: Double = 0.0,

    var isActive: Boolean = true
)