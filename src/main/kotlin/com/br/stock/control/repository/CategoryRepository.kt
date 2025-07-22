package com.br.stock.control.repository

import com.br.stock.control.model.entity.Category
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface CategoryRepository : MongoRepository<Category, ObjectId> {
}