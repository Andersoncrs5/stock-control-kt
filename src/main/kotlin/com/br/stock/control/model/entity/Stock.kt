package com.br.stock.control.model.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "stocks")
data class Stock(
    @Id
    var id: String,
    var product: String,
    var address: String,
    var quantity: Integer,
    var locationName: String,
    var lastMovementAt: LocalDateTime,
    var responsibleUserId: String,
    var warehouse: String,
    var isActive: Boolean = true,
    @CreatedDate
    var createdAt: LocalDateTime,
    @LastModifiedDate
    var updatedAt: LocalDateTime
)
