package com.br.stock.control.unitTest.service

import com.br.stock.control.model.entity.StockMovement
import com.br.stock.control.model.enum.MovementTypeEnum
import com.br.stock.control.repository.StockMovementRepository
import com.br.stock.control.service.StockMovementService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.whenever
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.time.LocalDate
import java.util.Optional
import java.util.Random
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class StockMovementServiceTest {
    @Mock private lateinit var repository: StockMovementRepository
    @InjectMocks private lateinit var service: StockMovementService

    private val stockMovement = StockMovement(
        id = UUID.randomUUID().toString(),
        stockId = UUID.randomUUID().toString(),
        productId = UUID.randomUUID().toString(),
        movementType = MovementTypeEnum.IN,
        quantity = Random().nextLong(),
        reason = null,
        responsibleUserId = UUID.randomUUID().toString(),
        notes = null,
        createdAt = LocalDate.now(),
        version = 0
    )

    @Test
    fun `should get stockMovement`() {
        whenever(repository.findById(stockMovement.id as String)).thenReturn(Optional.of(stockMovement))

        val result: Optional<StockMovement> = service.get(stockMovement.id as String )

        assertThat(result.isPresent).isTrue

        verify(repository, times(1)).findById((stockMovement.id) as String )
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should return null when get stockMovement`() {
        whenever(repository.findById(stockMovement.id as String)).thenReturn(Optional.empty())

        val result: Optional<StockMovement> = service.get(stockMovement.id as String )

        assertThat(result.isEmpty).isTrue

        verify(repository, times(1)).findById((stockMovement.id) as String )
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should delete stock movement`() {
        doNothing().whenever(repository).delete(stockMovement)

        this.service.delete(stockMovement)

        verify(repository, times(1)).delete(stockMovement)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should delete many stock movement`() {
        val ids: List<String> = List(10) { UUID.randomUUID().toString() }
        doNothing().whenever(repository).deleteAllById(ids)

        this.service.deleteMany(ids)

        verify(repository, times(1)).deleteAllById(ids)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should create a stockMovement`() {
        val moveCopy: StockMovement = stockMovement.copy(id = null)
        whenever(repository.save(moveCopy)).thenReturn(stockMovement)

        val result = this.service.create(moveCopy)

        assertThat(result.id).isNotNull

        verify(repository, times(1)).save(moveCopy)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should return paged list of stock movements with filters`() {
        val pageable: Pageable = PageRequest.of(0, 5)
        val movements = listOf(stockMovement)
        val page: Page<StockMovement> = PageImpl(movements, pageable, movements.size.toLong())

        whenever(
            repository.findAll(
                stockId = stockMovement.stockId,
                productId = stockMovement.productId,
                movementType = stockMovement.movementType,
                minQuantity = 1L,
                maxQuantity = 100L,
                reason = null,
                responsibleUserId = stockMovement.responsibleUserId,
                notes = null,
                createdAtBefore = LocalDate.now(),
                createdAtAfter = LocalDate.now().minusDays(10),
                pageable = pageable
            )
        ).thenReturn(page)

        val result = service.findAll(
            stockId = stockMovement.stockId,
            productId = stockMovement.productId,
            movementType = stockMovement.movementType,
            minQuantity = 1L,
            maxQuantity = 100L,
            reason = null,
            responsibleUserId = stockMovement.responsibleUserId,
            notes = null,
            createdAtBefore = LocalDate.now(),
            createdAtAfter = LocalDate.now().minusDays(10),
            pageable = pageable
        )

        assertThat(result.content).hasSize(1)
        assertThat(result.content[0].id).isEqualTo(stockMovement.id)
        assertThat(result.content[0].movementType).isEqualTo(MovementTypeEnum.IN)

        verify(repository, times(1)).findAll(
            stockMovement.stockId,
            stockMovement.productId,
            stockMovement.movementType,
            1L,
            100L,
            null,
            stockMovement.responsibleUserId,
            null,
            LocalDate.now(),
            LocalDate.now().minusDays(10),
            pageable
        )
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should delete all by stockId`() {
        val id = UUID.randomUUID().toString()
        doNothing().whenever(repository).deleteAllByStockId(id)

        this.service.deleteManyByStockId(id)

        verify(repository, times(1)).deleteAllByStockId(id)
        verifyNoMoreInteractions(repository)
    }


}