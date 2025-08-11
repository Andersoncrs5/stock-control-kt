package com.br.stock.control.repository

import com.br.stock.control.model.entity.Category
import com.br.stock.control.util.filters.category.CategoryCustomRepository
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.Optional

interface CategoryRepository : MongoRepository<Category, String>, CategoryCustomRepository {
    fun findByName(name: String): Optional<Category>
}