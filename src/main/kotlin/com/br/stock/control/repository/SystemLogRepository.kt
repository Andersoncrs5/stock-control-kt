package com.br.stock.control.repository

import com.br.stock.control.model.entity.SystemLog
import org.springframework.data.mongodb.repository.MongoRepository

interface SystemLogRepository: MongoRepository<SystemLog, String> {
}