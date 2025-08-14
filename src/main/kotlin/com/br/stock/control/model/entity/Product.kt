package com.br.stock.control.model.entity

import com.br.stock.control.model.enum.UnitOfMeasureEnum
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.time.LocalDateTime

@Document(collection = "products")
data class Product(
    @Id
    var id: String = "",
    @Indexed(unique = true)
    var name: String = "",
    var description: String = "",
    var sku: String = "",
    var barcode: String = "",
    var categoryId: String = "",
    var unitOfMeasure: UnitOfMeasureEnum = UnitOfMeasureEnum.UNIT,
    var price: BigDecimal = BigDecimal.valueOf(0.0),
    var cost: BigDecimal = BigDecimal.valueOf(0.0),
    var imageUrl: String? = "",
    var isActive: Boolean = true,
    var minStockLevel: Int = 0,
    var maxStockLevel: Int = 0,
    var locationSpecificStock: Map<String, Int> = mapOf(),
    @Version
    var version: Long = 0,
    @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @LastModifiedDate
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
