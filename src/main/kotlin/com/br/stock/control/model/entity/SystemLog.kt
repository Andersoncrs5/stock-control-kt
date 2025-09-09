package com.br.stock.control.model.entity

import com.br.stock.control.model.enum.EntityTypeEnum
import com.br.stock.control.model.enum.EventTypeEnum
import com.br.stock.control.model.enum.LevelEnum
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document(collection = "logs")
data class SystemLog(
    @Id
    var id: String,
    var timestamp: LocalDate,
    var level: LevelEnum,
    var message: String,
    var eventType: EventTypeEnum,
    var entityType: EntityTypeEnum,
    var entityId: String,
    var userId: String,
    var ipAddress: String,
    var details: Map<String, Any>,
    @Version
    var version: Long,
    @CreatedDate
    var createdAt: LocalDate,
)
