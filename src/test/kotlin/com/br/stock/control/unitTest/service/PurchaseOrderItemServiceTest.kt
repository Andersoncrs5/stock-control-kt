package com.br.stock.control.unitTest.service

import com.br.stock.control.model.dto.purchaseOrderItem.UpdateOrderItemDTO
import com.br.stock.control.model.entity.PurchaseOrderItem
import com.br.stock.control.repository.PurchaseOrderItemRepository
import com.br.stock.control.service.PurchaseOrderItemService
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
import java.math.BigDecimal
import java.time.LocalDate
import java.util.Optional
import java.util.UUID
import kotlin.String

@ExtendWith(MockitoExtension::class)
class PurchaseOrderItemServiceTest {
    @Mock private lateinit var repository: PurchaseOrderItemRepository
    @InjectMocks private lateinit var service: PurchaseOrderItemService

    val itemMock = PurchaseOrderItem(
        id = UUID.randomUUID().toString(),
        purchaseOrderId = UUID.randomUUID().toString(),
        productId = UUID.randomUUID().toString(),
        quantity = 10000,
        unitPrice = BigDecimal.valueOf(0.0),
        expectedQuantity = 10000,
        backOrderedQuantity = 1000,
        receivedQuantity = 9000,
        version = 0,
        createdAt = LocalDate.now().minusWeeks(1),
        updatedAt = LocalDate.now()
    )

    @Test fun `should return PurchaseOrderItem`() {
        whenever(repository.findById(itemMock.id as String)).thenReturn(Optional.of(itemMock))

        val result = this.service.get(itemMock.id as String)

        assertThat(result.isPresent).isTrue
            .withFailMessage("PurchaseOrderItem is null")

        assertThat(result.get().id).isEqualTo(itemMock.id)
            .withFailMessage("Id are different")

        verify(repository, times(1)).findById(itemMock.id as String)
        verifyNoMoreInteractions(repository)
    }

    @Test fun `should return null PurchaseOrderItem`() {
        whenever(repository.findById(itemMock.id as String)).thenReturn(Optional.empty())

        val result = this.service.get(itemMock.id as String)

        assertThat(result.isEmpty).isTrue
            .withFailMessage("PurchaseOrderItem is not null")

        verify(repository, times(1)).findById(itemMock.id as String)
        verifyNoMoreInteractions(repository)
    }

    @Test fun `should delete PurchaseOrderItem`() {
        doNothing().whenever(repository).delete(itemMock)

        this.service.delete(itemMock)

        verify(repository, times(1)).delete(itemMock)
        verifyNoMoreInteractions(repository)
    }

    @Test fun `should delete many PurchaseOrderItem`() {
        val id = List(10) { UUID.randomUUID().toString() }
        doNothing().whenever(repository).deleteAllById(id)

        this.service.deleteMany(id)

        verify(repository,times(1)).deleteAllById(id)
        verifyNoMoreInteractions(repository)
    }

    @Test fun `should create new PurchaseOrderItem`() {
        val itemCopy = this.itemMock.copy(id = null)
        whenever(repository.save(itemCopy)).thenReturn(itemMock)

        val orderItem = this.service.create(itemCopy)

        assertThat(orderItem.id).isNotNull.isNotBlank
            .withFailMessage("Id is null or black")

        assertThat(orderItem.id).isEqualTo(itemMock.id)
            .withFailMessage("Id are different")

        verify(repository,times(1)).save(itemCopy)
        verifyNoMoreInteractions(repository)
    }

    @Test fun `should update PurchaseOrderItem`() {
        val dto = UpdateOrderItemDTO(
            productId = UUID.randomUUID().toString(),
            quantity = 3456,
            unitPrice = BigDecimal.valueOf(77.9),
            expectedQuantity = 3456,
            backOrderedQuantity = 300,
            receivedQuantity = 456,
        )

        val itemCopy = itemMock.copy(
            productId = dto.productId,
            quantity = dto.quantity,
            unitPrice = dto.unitPrice,
            expectedQuantity = dto.expectedQuantity,
            backOrderedQuantity = dto.backOrderedQuantity,
            receivedQuantity = dto.receivedQuantity,
        )

        whenever(repository.save(itemCopy)).thenReturn(itemCopy)

        val orderItem = this.service.update(itemMock, dto)

        verify(repository, times(1)).save(itemMock)
        verifyNoMoreInteractions(repository)

        assertThat(orderItem).isNotNull
            .withFailMessage("Order is null")
        assertThat(orderItem.productId).isEqualTo(dto.productId)
            .withFailMessage("productId are different")
        assertThat(orderItem.quantity).isEqualTo(dto.quantity)
            .withFailMessage("quantity are different")
        assertThat(orderItem.unitPrice).isEqualByComparingTo(dto.unitPrice)
            .withFailMessage("unitPrice are different")
        assertThat(orderItem.expectedQuantity).isEqualTo(dto.expectedQuantity)
            .withFailMessage("expectedQuantity are different")
        assertThat(orderItem.backOrderedQuantity).isEqualTo(dto.backOrderedQuantity)
            .withFailMessage("backOrderedQuantity are different")
        assertThat(orderItem.receivedQuantity).isEqualTo(dto.receivedQuantity)
            .withFailMessage("receivedQuantity are different")
        assertThat(orderItem.updatedAt).isEqualTo(LocalDate.now())
            .withFailMessage("updatedAt was not updated")
        assertThat(orderItem.createdAt).isBefore(LocalDate.now())
            .withFailMessage("createdAt was updated")

        assertThat(orderItem.id).isEqualTo(itemMock.id)
        assertThat(orderItem.purchaseOrderId).isEqualTo(itemMock.purchaseOrderId)
    }


}