package com.br.stock.control.service

import com.br.stock.control.model.entity.Product
import com.br.stock.control.repository.ProductRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.Optional

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
    fun findAll(
        name: String?,
        min: BigDecimal?, max: BigDecimal?,
        pageNumber: Int, pageSize: Int
    ): Page<Product> {
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

    @Transactional(readOnly = true)
    fun getBySku(sku: String): Optional<Product> {
        logger.debug("Getting product by sku...")
        val product: Optional<Product> = this.productRepository.findBySku(sku)
        return product
    }

    @Transactional(readOnly = true)
    fun getByBarcode(barcode: String): Optional<Product> {
        logger.debug("Getting product by barcode...")
        val product: Optional<Product> = this.productRepository.findByBarcode(barcode)
        return product
    }

}