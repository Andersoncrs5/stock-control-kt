package com.br.stock.control.service

import com.br.stock.control.model.dto.product.UpdateProductDTO
import com.br.stock.control.model.entity.Product
import com.br.stock.control.model.enum.UnitOfMeasureEnum
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
    fun get(id: String): Product? {
        logger.debug("Getting product in database....")
        val product: Product? = this.productRepository.findById(id).orElse(null)
        logger.debug("Product found! Returning.....")
        return product
    }

    @Transactional
    fun delete(product: Product) {
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
        name: String?, sku: String?, barcode: String?,
        categoryId: String?, unitOfMeasure: UnitOfMeasureEnum?,
        minPrice: BigDecimal?, maxPrice: BigDecimal?,
        minCost: BigDecimal?, maxCost: BigDecimal?,
        isActive: Boolean?, pageNumber: Int, pageSize: Int
    ): Page<Product> {
        val pageable: PageRequest = PageRequest.of(pageNumber, pageSize)
        return productRepository.findWithFilters(
            name, sku, barcode, categoryId, unitOfMeasure,
            minPrice, maxPrice, minCost, maxCost,
            isActive, pageable
        )
    }

    @Transactional
    fun deleteMany(ids: List<String>) {
        logger.debug("Delete many product by id...")
        this.productRepository.deleteAllById(ids)
        logger.debug("Products deleted")
    }

    fun update(product: Product, toMerge: UpdateProductDTO): Product  {
        logger.debug("Updating product")
        product.name = toMerge.name
        product.description = toMerge.description
        product.sku = toMerge.sku
        product.barcode = toMerge.barcode
        product.unitOfMeasure = toMerge.unitOfMeasure
        product.price = toMerge.price
        product.cost = toMerge.cost
        product.imageUrl = toMerge.imageUrl
        product.minStockLevel = toMerge.minStockLevel
        product.maxStockLevel = toMerge.maxStockLevel
        product.locationSpecificStock = toMerge.locationSpecificStock

        val save = this.productRepository.save(product)
        logger.debug("Product updated")
        return save
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

    @Transactional
    fun changeStatus(product: Product): Product {
        logger.debug("Changing status product")
        product.isActive = !product.isActive

        return this.productRepository.save(product)
    }

}