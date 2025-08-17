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
import org.mockito.kotlin.whenever
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
        movementType= MovementTypeEnum.IN,
        quantity = Random().nextLong(),
        reason = null,
        responsibleUserId = UUID.randomUUID().toString(),
        createdAt= LocalDate.now()
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

}