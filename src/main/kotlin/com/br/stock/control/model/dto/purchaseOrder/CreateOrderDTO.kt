package com.br.stock.control.model.dto.purchaseOrder

import com.br.stock.control.model.enum.CurrencyEnum
import java.time.LocalDate

data class CreateOrderDTO(
    var supplierId: String = "",
    var expectedDeliveryDate: LocalDate? = null,
    var currency: CurrencyEnum = CurrencyEnum.NONE ,
    var notes: String? = null,
)