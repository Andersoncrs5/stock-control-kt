package com.br.stock.control.model.entity

import com.br.stock.control.model.enum.MovementTypeEnum
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document(collection = "stock_movements")
data class StockMovement(
    @Id
    var id: String? = null,

    var stockId: String = "",
    var productId: String = "",
    var movementType: MovementTypeEnum? = null,
    var quantity: Long = 0,
    var reason: String? = null,

    var responsibleUserId: String = "",
    var notes: String? = null,

    @Version
    var version: Long = 0,

    @CreatedDate
    var createdAt: LocalDate = LocalDate.now()
)

