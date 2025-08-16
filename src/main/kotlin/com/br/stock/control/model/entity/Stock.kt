package com.br.stock.control.model.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document(collection = "stocks")
data class Stock(
    @Id
    var id: String,
    var productId: String,
    var addressId: String,
    var quantity: Integer,
    var locationName: String,
    var lastMovementAt: LocalDate,
    var responsibleUserId: String,
    var warehouseId: String,
    var isActive: Boolean = true,
    @CreatedDate
    var createdAt: LocalDate,
    @LastModifiedDate
    var updatedAt: LocalDate
)
