package com.br.stock.control.repository

import com.br.stock.control.model.entity.Product
import com.br.stock.control.util.filters.products.ProductCustomRepository
import org.springframework.data.mongodb.repository.MongoRepository

interface ProductRepository: MongoRepository<Product, String>, ProductCustomRepository {
}