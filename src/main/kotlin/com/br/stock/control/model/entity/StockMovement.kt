package com.br.stock.control.model.entity

import com.br.stock.control.model.enum.MovementTypeEnum
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document(collection = "stock_movements")
data class StockMovement(
    @Id
    var id: String,
    var productId: String,
    var quantity: Integer,
    var movementType: MovementTypeEnum,
    var sourceLocation: String,
    var destinationLocation: String,
    var movementDate: LocalDate,
    var moveByUserId: String,
    var senderByUserId: String,
    var reason: String? = null,
    var referenceDocument: String? = null,
    var notes: String,
    @Version
    var version: Long,
    @CreatedDate
    var createdAt: LocalDate,
    @LastModifiedDate
    var updatedAt: LocalDate
)
