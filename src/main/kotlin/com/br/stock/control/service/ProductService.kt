package com.br.stock.control.service

import com.br.stock.control.model.entity.Product
import com.br.stock.control.repository.ProductRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

@Service
class ProductService(
    private val productRepository: ProductRepository,
) {
    private val logger = LoggerFactory.getLogger(ProductService::class.java)

    @Transactional(readOnly = true)
    fun getProduct(id: String): Product? {
        logger.debug("Getting product in database....")
        val product: Product? = this.productRepository.findById(id).orElse(null)
        logger.debug("Product found! Returning.....")
        return product
    }

    @Transactional
    fun deleteProduct(product: Product) {
        logger.debug("Deleting product....")
        this.productRepository.delete(product)
        logger.debug("Product deleted!")
    }

    @Transactional(readOnly = true)
    fun save(product: Product): Product {
        logger.debug("Saving product in database...")
        val result: Product = this.productRepository.save<Product>(product)
        logger.debug("Product saved!")
        return result
    }

    @Transactional(readOnly = true)
    fun findAll(name: String?, min: BigDecimal?, max: BigDecimal?, pageNumber: Int, pageSize: Int): Page<Product> {
        val pageable = PageRequest.of(pageNumber, pageSize)
        val result: Page<Product> = productRepository.findWithFilters(name, min, max, pageable)
        return result
    }

    @Transactional
    fun deleteMany(ids: List<String>) {
        logger.debug("Delete many product by id...")
        this.productRepository.deleteAllById(ids)
        logger.debug("Products deleted")
    }

    fun mergeProductV2(product: Product, toMerge: Product): Product {
        Product::class.memberProperties
            .filterIsInstance<KMutableProperty1<Product, Any?>>()
            .forEach { prop ->
                prop.isAccessible = true
                prop.set(product, prop.get(toMerge))
            }
        return product
    }

    fun mergeProduct(product: Product, toMerge: Product): Product = product.apply {
        name = toMerge.name
        description = toMerge.description
        sku = toMerge.sku
        barcode = toMerge.barcode
        unitOfMeasure = toMerge.unitOfMeasure
        price = toMerge.price
        cost = toMerge.cost
        imageUrl = toMerge.imageUrl
        isActive = toMerge.isActive
        minStockLevel = toMerge.minStockLevel
        maxStockLevel = toMerge.maxStockLevel
        locationSpecificStock = toMerge.locationSpecificStock
    }


}