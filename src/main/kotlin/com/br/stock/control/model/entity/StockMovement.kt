package com.br.stock.control.model.entity

import com.br.stock.control.model.enum.MovementTypeEnum
import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "stock_movements")
data class StockMovement(
    @Id
    var id: ObjectId,
    var product: ObjectId,
    var quantity: Integer,
    var movementType: MovementTypeEnum,
    var sourceLocation: ObjectId,
    var destinationLocation: ObjectId,
    var movementDate: LocalDateTime,
    var moveByUser: ObjectId,
    var senderByUser: ObjectId,
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
