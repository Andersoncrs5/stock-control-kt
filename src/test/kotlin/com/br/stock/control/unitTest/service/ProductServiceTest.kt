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
import org.assertj.core.api.Assertions.assertThat

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

        val result = productService.get(product.id);

        assertNotNull(result, "Result of productService getProduct came null")
        assertEquals(result.id, product.id, "IDs of products are different")
        assertEquals(result.name, product.name, "Names of products are different")

        verify(productRepository, times(1)).findById(product.id)
        verifyNoMoreInteractions(productRepository)
    }

    @Test
    fun `should return null in getProduct`() {
        whenever(productRepository.findById("1")).thenReturn(Optional.empty())

        val result = productService.get("1")

        assertNull(result, "Result of productService getProduct not came null")

        verify(productRepository, times(1)).findById("1")
        verifyNoMoreInteractions(productRepository)
    }

    @Test
    fun `should delete product`() {
        doNothing().whenever(productRepository).delete(product)

        productService.delete(product)
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
    fun `should return paged list of products with advanced filters`() {
        val productList: List<Product> = List(5) { product.copy(id = UUID.randomUUID().toString()) }
        val pageable = PageRequest.of(0, 5)
        val page = PageImpl(productList, pageable, productList.size.toLong())

        whenever(
            productRepository.findWithFilters(
                "Prod", "SKU", "BARCODE",
                "CAT123", UnitOfMeasureEnum.UNIT,
                BigDecimal("10.00"), BigDecimal("50.00"),
                BigDecimal("5.00"), BigDecimal("25.00"),
                true, pageable
            )
        ).thenReturn(page)

        val result = productService.findAll(
            "Prod", "SKU", "BARCODE", "CAT123", UnitOfMeasureEnum.UNIT,
            BigDecimal("10.00"), BigDecimal("50.00"),
            BigDecimal("5.00"), BigDecimal("25.00"),
            true, 0, 5
        )

        assertEquals(5, result.content.size)
        assertEquals(product.name, result.content[0].name)
        verify(productRepository, times(1)).findWithFilters(
            "Prod", "SKU", "BARCODE", "CAT123", UnitOfMeasureEnum.UNIT,
            BigDecimal("10.00"), BigDecimal("50.00"),
            BigDecimal("5.00"), BigDecimal("25.00"),
            true, pageable
        )
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

    @Test
    fun `should return product when get by sku`() {
        whenever(productRepository.findBySku(product.sku)).thenReturn(Optional.of(product))

        val result: Optional<Product> = productService.getBySku(product.sku)

        assertThat(result.isPresent).isTrue.withFailMessage("Result is empty")
        assertEquals(result.get().id, product.id, "IDs of products are different")
        assertEquals(result.get().name, product.name, "Names of products are different")
        assertThat(result.get().sku).isEqualTo(product.sku).withFailMessage("Skus are different")

        verify(productRepository, times(1)).findBySku(product.sku)
        verifyNoMoreInteractions(productRepository)
    }

    @Test
    fun `should return null in get product by sku`() {
        whenever(productRepository.findBySku(product.sku)).thenReturn(Optional.empty())

        val result: Optional<Product> = productService.getBySku(product.sku)

        assertThat(result.isEmpty).isTrue

        verify(productRepository, times(1)).findBySku(product.sku)
        verifyNoMoreInteractions(productRepository)
    }

    @Test
    fun `should return product when get by barcode`() {
        whenever(productRepository.findByBarcode(product.barcode)).thenReturn(Optional.of(product))

        val result: Optional<Product> = productService.getByBarcode(product.barcode)

        assertThat(result.isPresent).isTrue.withFailMessage("Result is empty")
        assertEquals(result.get().id, product.id, "IDs of products are different")
        assertThat(result.get().barcode).isEqualTo(product.barcode).withFailMessage("barcodes are different")

        verify(productRepository, times(1)).findByBarcode(product.barcode)
        verifyNoMoreInteractions(productRepository)
    }

    @Test
    fun `should return null in get product by barcode`() {
        whenever(productRepository.findByBarcode(product.barcode)).thenReturn(Optional.empty())

        val result: Optional<Product> = productService.getByBarcode(product.barcode)

        assertThat(result.isEmpty).isTrue.withFailMessage("Product is present")

        verify(productRepository, times(1)).findByBarcode(product.barcode)
        verifyNoMoreInteractions(productRepository)
    }

    @Test
    fun `should change product status`() {
        val activeProduct = product.copy(isActive = true)
        val deactivatedProduct = activeProduct.copy(isActive = false)

        whenever(productRepository.save(activeProduct)).thenReturn(deactivatedProduct)

        val result = productService.changeStatus(activeProduct)

        assertNotNull(result, "Result of productService changeStatus came null")
        assertEquals(deactivatedProduct.isActive, result.isActive, "Status of product did not change correctly")

        verify(productRepository, times(1)).save(activeProduct)
        verifyNoMoreInteractions(productRepository)
    }

    @Test
    fun `should activate an inactive product`() {
        val inactiveProduct = product.copy(isActive = false)
        val activatedProduct = inactiveProduct.copy(isActive = true)

        whenever(productRepository.save(inactiveProduct)).thenReturn(activatedProduct)

        val result = productService.changeStatus(inactiveProduct)

        assertNotNull(result, "Result of productService changeStatus came null")
        assertEquals(activatedProduct.isActive, result.isActive, "Status of product did not change correctly")

        verify(productRepository, times(1)).save(inactiveProduct)
        verifyNoMoreInteractions(productRepository)
    }

}