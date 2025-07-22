package com.br.stock.control.model.entity

import com.br.stock.control.model.enum.UnitOfMeasureEnum
import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.time.LocalDateTime

@Document(collection = "products")
data class Product(
    @Id
    var id: ObjectId,
    var name: String,
    var description: String,
    var sku: String,
    var barcode: String,
    var category: Category,
    var unitOfMeasure: UnitOfMeasureEnum,
    var price: BigDecimal,
    var cost: BigDecimal,
    var imageUrl: String,
    var isActive: Boolean,
    var minStockLevel: Integer,
    var maxStockLevel: Integer,
    var locationSpecificStock: Map<String, Integer>,
    @Version
    var version: Long,
    @CreatedDate
    var createdAt: LocalDateTime,
    @LastModifiedDate
    var updatedAt: LocalDateTime
)
