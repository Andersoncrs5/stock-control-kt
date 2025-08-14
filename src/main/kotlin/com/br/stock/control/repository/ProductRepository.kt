package com.br.stock.control.repository

import com.br.stock.control.model.entity.Product
import com.br.stock.control.util.filters.products.ProductCustomRepository
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.Optional

interface ProductRepository: MongoRepository<Product, String>, ProductCustomRepository {
    fun findBySku(sku: String): Optional<Product>
    fun findByBarcode(barcode: String): Optional<Product>
}