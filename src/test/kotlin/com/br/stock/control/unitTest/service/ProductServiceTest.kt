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
import java.time.LocalDate
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

    @Mock private lateinit var repository: ProductRepository

    @InjectMocks private lateinit var service: ProductService

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
        createdAt = LocalDate.now(),
        updatedAt = LocalDate.now()
    )

    @Test
    fun `should getProduct`() {
        whenever(repository.findById(product.id)).thenReturn(Optional.of<Product>(product))

        val result = service.get(product.id);

        assertNotNull(result, "Result of productService getProduct came null")
        assertEquals(result.id, product.id, "IDs of products are different")
        assertEquals(result.name, product.name, "Names of products are different")

        verify(repository, times(1)).findById(product.id)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should return null in getProduct`() {
        whenever(repository.findById("1")).thenReturn(Optional.empty())

        val result = service.get("1")

        assertNull(result, "Result of productService getProduct not came null")

        verify(repository, times(1)).findById("1")
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should delete product`() {
        doNothing().whenever(repository).delete(product)

        service.delete(product)
        verify(repository, times(1)).delete(product)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should save Product`() {
        val productAfter: Product = product.copy(id = UUID.randomUUID().toString())
        whenever(repository.save(productAfter)).thenReturn(productAfter)

        val result = service.save(productAfter)

        assertNotNull(result, "Result of productService getProduct came null")
        assertEquals(result.id, productAfter.id, "IDs of products are different")
        assertEquals(result.name, productAfter.name, "Names of products are different")

        verify(repository, times(1)).save(productAfter)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should return paged list of products with advanced filters`() {
        val productList: List<Product> = List(5) { product.copy(id = UUID.randomUUID().toString()) }
        val pageable = PageRequest.of(0, 5)
        val page = PageImpl(productList, pageable, productList.size.toLong())

        val createdAtBefore = LocalDate.now().plusDays(1)  // pode ser qualquer valor válido
        val createdAtAfter = LocalDate.now().minusDays(30)

        whenever(
            repository.findWithFilters(
                "Prod", "SKU", "BARCODE",
                "CAT123", UnitOfMeasureEnum.UNIT,
                BigDecimal("10.00"), BigDecimal("50.00"),
                BigDecimal("5.00"), BigDecimal("25.00"),
                true, createdAtBefore, createdAtAfter, pageable
            )
        ).thenReturn(page)

        val result = service.findAll(
            "Prod", "SKU", "BARCODE", "CAT123", UnitOfMeasureEnum.UNIT,
            BigDecimal("10.00"), BigDecimal("50.00"),
            BigDecimal("5.00"), BigDecimal("25.00"),
            createdAtBefore, createdAtAfter, true, 0, 5
        )

        assertEquals(5, result.content.size)
        assertEquals(product.name, result.content[0].name)
        verify(repository, times(1)).findWithFilters(
            "Prod", "SKU", "BARCODE", "CAT123", UnitOfMeasureEnum.UNIT,
            BigDecimal("10.00"), BigDecimal("50.00"),
            BigDecimal("5.00"), BigDecimal("25.00"),
            true, createdAtBefore, createdAtAfter, pageable
        )
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should delete many products`() {
        val ids = List(10) { UUID.randomUUID().toString() }

        doNothing().whenever(repository).deleteAllById(ids)

        this.service.deleteMany(ids)

        verify(repository, times(1)).deleteAllById(ids)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should return product when get by sku`() {
        whenever(repository.findBySku(product.sku)).thenReturn(Optional.of(product))

        val result: Optional<Product> = service.getBySku(product.sku)

        assertThat(result.isPresent).isTrue.withFailMessage("Result is empty")
        assertEquals(result.get().id, product.id, "IDs of products are different")
        assertEquals(result.get().name, product.name, "Names of products are different")
        assertThat(result.get().sku).isEqualTo(product.sku).withFailMessage("Skus are different")

        verify(repository, times(1)).findBySku(product.sku)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should return null in get product by sku`() {
        whenever(repository.findBySku(product.sku)).thenReturn(Optional.empty())

        val result: Optional<Product> = service.getBySku(product.sku)

        assertThat(result.isEmpty).isTrue

        verify(repository, times(1)).findBySku(product.sku)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should return product when get by barcode`() {
        whenever(repository.findByBarcode(product.barcode)).thenReturn(Optional.of(product))

        val result: Optional<Product> = service.getByBarcode(product.barcode)

        assertThat(result.isPresent).isTrue.withFailMessage("Result is empty")
        assertEquals(result.get().id, product.id, "IDs of products are different")
        assertThat(result.get().barcode).isEqualTo(product.barcode).withFailMessage("barcodes are different")

        verify(repository, times(1)).findByBarcode(product.barcode)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should return null in get product by barcode`() {
        whenever(repository.findByBarcode(product.barcode)).thenReturn(Optional.empty())

        val result: Optional<Product> = service.getByBarcode(product.barcode)

        assertThat(result.isEmpty).isTrue.withFailMessage("Product is present")

        verify(repository, times(1)).findByBarcode(product.barcode)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should change product status`() {
        val activeProduct = product.copy(isActive = true)
        val deactivatedProduct = activeProduct.copy(isActive = false)

        whenever(repository.save(activeProduct)).thenReturn(deactivatedProduct)

        val result = service.changeStatus(activeProduct)

        assertNotNull(result, "Result of productService changeStatus came null")
        assertEquals(deactivatedProduct.isActive, result.isActive, "Status of product did not change correctly")

        verify(repository, times(1)).save(activeProduct)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should activate an inactive product`() {
        val inactiveProduct = product.copy(isActive = false)
        val activatedProduct = inactiveProduct.copy(isActive = true)

        whenever(repository.save(inactiveProduct)).thenReturn(activatedProduct)

        val result = service.changeStatus(inactiveProduct)

        assertNotNull(result, "Result of productService changeStatus came null")
        assertEquals(activatedProduct.isActive, result.isActive, "Status of product did not change correctly")

        verify(repository, times(1)).save(inactiveProduct)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should return true  when check exists by id`() {
        whenever(repository.existsById("1")).thenReturn(true)

        val result = this.service.existsById("1")

        assertThat(result).isTrue

        verify(repository, times(1)).existsById("1")
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should return false when check exists by id`() {
        whenever(repository.existsById("1")).thenReturn(false)

        val result = this.service.existsById("1")

        assertThat(result).isFalse

        verify(repository, times(1)).existsById("1")
        verifyNoMoreInteractions(repository)
    }


}