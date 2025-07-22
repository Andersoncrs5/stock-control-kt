package com.br.stock.control.model.entity

import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import java.time.LocalDateTime

data class Category(
    @Id
    var id: ObjectId,
    var name: String,
    var description: String?,
    var parentCategory: ObjectId,
    @Version
    var version: Long,
    @CreatedDate
    var createdAt: LocalDateTime,
    @LastModifiedDate
    var updatedAt: LocalDateTime
)
