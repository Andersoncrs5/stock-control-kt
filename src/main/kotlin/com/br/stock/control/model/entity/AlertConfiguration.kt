package com.br.stock.control.model.entity

import com.br.stock.control.model.enum.AlertTypeEnum
import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import java.time.LocalDateTime

data class AlertConfiguration(
    @Id
    var id: ObjectId,
    var alertType: AlertTypeEnum,
    var product: ObjectId,
    var location: Location,
    var threshold: Integer,
    var recipientEmails: List<String>,
    var messageTemplate: String,
    var isActive: Boolean,
    var lastTriggeredAt: LocalDateTime,
    @Version
    var version: Long,
    @CreatedDate
    var createdAt: LocalDateTime,
    @LastModifiedDate
    var updatedAt: LocalDateTime
)
