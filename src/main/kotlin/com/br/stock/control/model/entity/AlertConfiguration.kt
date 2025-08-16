package com.br.stock.control.model.entity

import com.br.stock.control.model.enum.AlertTypeEnum
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import java.time.LocalDate

data class AlertConfiguration(
    @Id
    var id: String?,
    var alertType: AlertTypeEnum,
    var productId: String,
    var threshold: Integer,
    var recipientEmails: List<String>,
    var messageTemplate: String,
    var isActive: Boolean,
    var lastTriggeredAt: LocalDate,
    @Version
    var version: Long,
    @CreatedDate
    var createdAt: LocalDate,
    @LastModifiedDate
    var updatedAt: LocalDate
)
