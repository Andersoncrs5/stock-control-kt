package com.br.stock.control.repository

import com.br.stock.control.model.entity.Product
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface ProductRepository: MongoRepository<Product, String> {
}