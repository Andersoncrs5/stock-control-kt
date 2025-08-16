package com.br.stock.control.model.entity

import com.br.stock.control.model.enum.WareHouseEnum
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "warehouses")
data class Warehouse(
    @Id
    var id: String = "",
    @Indexed(unique = true)
    var name: String = "",
    var description: String = "",
    var addressId: String = "",
    var responsibleUserId: String = "",
    var amount: Long = 0L,
    var capacityCubicMeters: Double = 0.0,
    var type: WareHouseEnum = WareHouseEnum.NONE,
    var isActive: Boolean = true,
    var canToAdd: Boolean = true,
    @Version
    var version: Long = 0,
    @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @LastModifiedDate
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
