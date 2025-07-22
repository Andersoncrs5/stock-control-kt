package com.br.stock.control.repository

import com.br.stock.control.model.entity.SystemLog
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface SystemLogRepository: MongoRepository<SystemLog, ObjectId> {
}