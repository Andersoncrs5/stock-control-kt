package com.br.stock.control.repository

import com.br.stock.control.model.entity.AlertConfiguration
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface AlertConfigurationRepository : MongoRepository<AlertConfiguration, String>