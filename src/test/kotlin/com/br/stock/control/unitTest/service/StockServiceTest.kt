package com.br.stock.control.unitTest.service

import com.br.stock.control.model.dto.stock.UpdateStockDTO
import com.br.stock.control.model.entity.Stock
import com.br.stock.control.repository.StockRepository
import com.br.stock.control.service.StockService
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.util.Optional
import java.util.Random
import java.util.UUID
import kotlin.test.Test
import org.assertj.core.api.Assertions.assertThat
import org.mockito.kotlin.doNothing

@ExtendWith(MockitoExtension::class)
class StockServiceTest {
    @Mock private lateinit var repository: StockRepository
    @InjectMocks private lateinit var service: StockService
    
    private val stockMock = Stock(
        id = UUID.randomUUID().toString(),
        productId = UUID.randomUUID().toString(),
        quantity = Random().nextLong(100000),
        lastMovementAt = LocalDate.now(),
        responsibleUserId = UUID.randomUUID().toString(),
        warehouseId = UUID.randomUUID().toString(),
        isActive = true,
        createdAt = LocalDate.now(),
        updatedAt = LocalDate.now()
    )

    @Test
    fun `should get the stock`() {
        whenever(repository.findById((stockMock.id) as String )).thenReturn(Optional.of(stockMock))

        val result: Optional<Stock> = service.get((stockMock.id) as String )

        assertThat(result.isPresent).isTrue

        verify(repository, times(1)).findById((stockMock.id) as String )
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should return null when get the stock`() {
        whenever(repository.findById((stockMock.id) as String )).thenReturn(Optional.empty())

        val result: Optional<Stock> = service.get((stockMock.id) as String )

        assertThat(result.isEmpty).isTrue

        verify(repository, times(1)).findById((stockMock.id) as String )
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should delete stock`() {
        doNothing().whenever(repository).delete(stockMock)

        this.service.delete(stockMock)

        verify(repository, times(1)).delete(stockMock)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should delete many stock with productId`() {
        val productId = UUID.randomUUID().toString()
        doNothing().whenever(repository).deleteAllByProductId(productId)

        this.service.deleteByProductId(productId)

        verify(repository, times(1)).deleteAllByProductId(productId)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should delete many stock with warehouseId`() {
        val id = UUID.randomUUID().toString()
        doNothing().whenever(repository).deleteAllByWarehouseId(id)

        this.service.deleteByWarehouseId(id)

        verify(repository, times(1)).deleteAllByWarehouseId(id)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should create new stock`() {
        val stock = stockMock.copy(id = null)
        whenever(repository.save(stock)).thenReturn(stockMock)

        val result: Stock = this.service.create(stock)

        assertThat(result.id).isNotNull
        assertThat(result.id).isEqualTo(stockMock.id)

        verify(repository, times(1)).save(stock)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should update stock`() {
        val stock = stockMock.copy()
        val dto = UpdateStockDTO(
            quantity = 1000,
            responsibleUserId = UUID.randomUUID().toString(),
            warehouseId = UUID.randomUUID().toString()
        )

        val updatedStock = stock.copy(
            quantity = dto.quantity,
            responsibleUserId = dto.responsibleUserId,
            warehouseId = dto.warehouseId
        )

        whenever(repository.save(stock)).thenReturn(updatedStock)

        val result: Stock = service.update(stock, dto)

        assertThat(result.id).isEqualTo(stockMock.id)
        assertThat(result.quantity).isEqualTo(dto.quantity)
        assertThat(result.responsibleUserId).isEqualTo(dto.responsibleUserId)
        assertThat(result.warehouseId).isEqualTo(dto.warehouseId)

        verify(repository, times(1)).save(stock)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should change status active`() {
        val stockCopy = stockMock.copy(isActive = false)
        whenever(repository.save(stockMock)).thenReturn(stockCopy)

        val result: Stock = this.service.changeStatus(stockMock)

        assertThat(result.id).isNotNull
        assertThat(result.id).isEqualTo(stockMock.id)
        assertThat(result.isActive).isFalse

        verify(repository, times(1)).save(stockMock)
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