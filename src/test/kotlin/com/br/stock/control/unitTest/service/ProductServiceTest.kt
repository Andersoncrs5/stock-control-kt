package com.br.stock.control.unitTest.service

import com.br.stock.control.model.entity.Product
import com.br.stock.control.model.enum.UnitOfMeasureEnum
import com.br.stock.control.repository.ProductRepository
import com.br.stock.control.service.ProductService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID
import org.junit.jupiter.api.*
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.util.Optional
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class ProductServiceTest {

    @Mock
    private lateinit var productRepository: ProductRepository

    @InjectMocks
    private lateinit var productService: ProductService

    val product: Product = Product(
        id = UUID.randomUUID().toString(),
        name = "product",
        description = "description product",
        sku = "0123456789",
        barcode = "789578294758027492",
        categoryId = UUID.randomUUID().toString(),
        unitOfMeasure = UnitOfMeasureEnum.UNIT,
        price = BigDecimal.valueOf(0.0),
        cost = BigDecimal.valueOf(0.0),
        imageUrl = "",
        isActive = true,
        minStockLevel = 1000,
        maxStockLevel = 10_000,
        locationSpecificStock = emptyMap(),
        version = 0,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )

    @Test
    fun `should getProduct`() {
        whenever(productRepository.findById(product.id)).thenReturn(Optional.of<Product>(product))

        val result = productService.getProduct(product.id);

        assertNotNull(result, "Result of productService getProduct came null")
        assertEquals(result.id, product.id, "IDs of products are different")
        assertEquals(result.name, product.name, "Names of products are different")

        verify(productRepository, times(1)).findById(product.id)
        verifyNoMoreInteractions(productRepository)
    }

    @Test
    fun `should return null in getProduct`() {
        whenever(productRepository.findById("1")).thenReturn(Optional.empty())

        val result = productService.getProduct("1")

        assertNull(result, "Result of productService getProduct not came null")

        verify(productRepository, times(1)).findById("1")
        verifyNoMoreInteractions(productRepository)
    }

    @Test
    fun `should delete product`() {
        doNothing().whenever(productRepository).delete(product)

        productService.deleteProduct(product)
        verify(productRepository, times(1)).delete(product)
        verifyNoMoreInteractions(productRepository)
    }

    @Test
    fun `should save Product`() {
        val productAfter: Product = product.copy(id = UUID.randomUUID().toString())
        whenever(productRepository.save(productAfter)).thenReturn(productAfter)

        val result = productService.save(productAfter)

        assertNotNull(result, "Result of productService getProduct came null")
        assertEquals(result.id, productAfter.id, "IDs of products are different")
        assertEquals(result.name, productAfter.name, "Names of products are different")

        verify(productRepository, times(1)).save(productAfter)
        verifyNoMoreInteractions(productRepository)
    }

    @Test
    fun `should return paged list of products with filters`() {
        val productList: List<Product> = List(10) { product.copy(id = UUID.randomUUID().toString()) }

        val name = "Product"
        val min = BigDecimal("5.00")
        val max = BigDecimal("30.00")
        val pageNumber = 0
        val pageSize = 2
        val pageable = PageRequest.of(pageNumber, pageSize)
        val page = PageImpl(productList, pageable, productList.size.toLong())

        whenever(
            productRepository.findWithFilters(name, min, max, pageable)
        ).thenReturn(page)


        val result = productService.findAll(name, min, max, pageNumber, pageSize)


        Assertions.assertEquals(10, result.content.size)
        Assertions.assertEquals(product.name, result.content[0].name)
        Assertions.assertEquals(product.name, result.content[1].name)
        verify(productRepository, times(1)).findWithFilters(name, min, max, pageable)
        verifyNoMoreInteractions(productRepository)
    }

    @Test
    fun `should delete many products`() {
        val ids = List(10) { UUID.randomUUID().toString() }

        doNothing().whenever(productRepository).deleteAllById(ids)

        this.productService.deleteMany(ids)

        verify(productRepository, times(1)).deleteAllById(ids)
        verifyNoMoreInteractions(productRepository)
    }

}