package com.br.stock.control.service

import com.br.stock.control.model.entity.Product
import com.br.stock.control.repository.ProductRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class ProductService(
    private val productRepository: ProductRepository,
) {
    private val logger = LoggerFactory.getLogger(ProductService::class.java)

    fun getProduct(id: String): Product? {
        logger.debug("Getting product in database....")
        val product: Product? = this.productRepository.findById(id).orElse(null)
        logger.debug("Poduct found! Returning.....")
        return product
    }

    fun deleteProduct(product: Product) {
        logger.debug("Deleting product....")
        this.productRepository.delete(product)
        logger.debug("Product deleted!")
    }

    fun save(product: Product): Product {
        logger.debug("Saving product in database...")
        val result: Product = this.productRepository.save<Product>(product)
        logger.debug("Product saved!")
        return result
    }

    fun findAll(name: String?, min: BigDecimal?, max: BigDecimal?, pageNumber: Int, pageSize: Int): Page<Product> {
        val pageable = PageRequest.of(pageNumber, pageSize)
        val result: Page<Product> = productRepository.findWithFilters(name, min, max, pageable)
        return result
    }

}