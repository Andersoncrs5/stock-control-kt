package com.br.stock.control.model.entity

import com.br.stock.control.model.enum.UnitOfMeasureEnum
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.time.LocalDate

@Document(collection = "products")
data class Product(
    @Id
    var id: String = "",
    @Indexed(unique = true)
    var name: String = "",
    var description: String = "",
    @Indexed(unique = true)
    var sku: String = "",
    @Indexed(unique = true)
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
    var createdAt: LocalDate = LocalDate.now(),
    @LastModifiedDate
    var updatedAt: LocalDate = LocalDate.now()
)
