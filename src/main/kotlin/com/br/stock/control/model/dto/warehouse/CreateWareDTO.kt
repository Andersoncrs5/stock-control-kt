package com.br.stock.control.model.dto.warehouse

import com.br.stock.control.model.enum.WareHouseEnum

data class CreateWareDTO(
    var name: String = "",
    var description: String = "",
    var addressId: String = "",
    var responsibleUserId: String = "",
    var amount: Long = 0L,
    var capacityCubicMeters: Double = 0.0,
    var type: WareHouseEnum = WareHouseEnum.NONE
)