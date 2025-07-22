package com.br.stock.control.model.entity

import com.br.stock.control.model.enum.EntityTypeEnum
import com.br.stock.control.model.enum.EventTypeEnum
import com.br.stock.control.model.enum.LevelEnum
import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "logs")
data class SystemLog(
    @Id
    var id: ObjectId,
    var timestamp: LocalDateTime,
    var level: LevelEnum,
    var message: String,
    var eventType: EventTypeEnum,
    var entityType: EntityTypeEnum,
    var entityId: ObjectId,
    var userId: ObjectId,
    var ipAddress: String,
    var details: Map<String, Any>,
    @Version
    var version: Long,
    @CreatedDate
    var createdAt: LocalDateTime,
    @LastModifiedDate
    var updatedAt: LocalDateTime
)
