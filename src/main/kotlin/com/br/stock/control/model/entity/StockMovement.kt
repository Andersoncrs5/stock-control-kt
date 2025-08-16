package com.br.stock.control.model.entity

import com.br.stock.control.model.enum.MovementTypeEnum
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "stock_movements")
data class StockMovement(
    @Id
    var id: String,
    var productId: String,
    var quantity: Integer,
    var movementType: MovementTypeEnum,
    var sourceLocation: String,
    var destinationLocation: String,
    var movementDate: LocalDateTime,
    var moveByUserId: String,
    var senderByUserId: String,
    var reason: String,
    var referenceDocument: String,
    var notes: String,
    @Version
    var version: Long,
    @CreatedDate
    var createdAt: LocalDateTime,
    @LastModifiedDate
    var updatedAt: LocalDateTime
)
