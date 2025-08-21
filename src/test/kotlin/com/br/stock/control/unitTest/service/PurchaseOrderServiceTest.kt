package com.br.stock.control.unitTest.service

import com.br.stock.control.model.entity.PurchaseOrder
import com.br.stock.control.model.enum.CurrencyEnum
import com.br.stock.control.model.enum.StatusEnum
import com.br.stock.control.repository.PurchaseOrderRepository
import com.br.stock.control.service.PurchaseOrderService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal
import java.time.LocalDate
import java.util.Optional
import java.util.UUID
import kotlin.random.Random

@ExtendWith(MockitoExtension::class)
class PurchaseOrderServiceTest {

    @Mock private lateinit var repository: PurchaseOrderRepository
    @InjectMocks private lateinit var service: PurchaseOrderService

    val orderMock = PurchaseOrder(
        id = "12345678",
        supplierId = UUID.randomUUID().toString(),
        expectedDeliveryDate = LocalDate.now().plusMonths(3),
        currency = CurrencyEnum.BTC,
        deliveryDate = LocalDate.now().plusMonths(2),
        status = StatusEnum.NONE,
        totalAmount = BigDecimal.valueOf(Random.nextDouble(999.99,9999.99)),
        shippingCost = BigDecimal.valueOf(Random.nextDouble(999.99,9999.99)),
        placedByUserId = UUID.randomUUID().toString(),
        approvedByUserId = UUID.randomUUID().toString(),
        approveAt = LocalDate.now(),
        canceledByUserId = UUID.randomUUID().toString(),
        canceledAt = LocalDate.now(),
        receivedAt = LocalDate.now(),
        reasonCancel = "i do not now",
        notes = "lorem notes",
        version = 0,
        createdAt = LocalDate.now(),
        updatedAt = LocalDate.now()
    )

    @Test
    fun `should get order`() {
        whenever(repository.findById(orderMock.id  as String )).thenReturn(Optional.of(orderMock))

        val result = this.service.get(orderMock.id as String )

        assertThat(result.isPresent).isTrue

        verify(repository, times(1)).findById((orderMock.id) as String )
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should not get order`() {
        whenever(repository.findById(orderMock.id as String )).thenReturn(Optional.empty())

        val result = this.service.get(orderMock.id as String )

        assertThat(result.isEmpty).isTrue

        verify(repository, times(1)).findById((orderMock.id) as String )
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should delete order`() {
        doNothing().whenever(repository).delete(orderMock)

        this.service.delete(orderMock)

        verify(repository, times(1)).delete(orderMock)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should delete many order`() {
        val ids = List(10) { UUID.randomUUID().toString() }
        doNothing().whenever(repository).deleteAllById(ids)

        this.service.deleteMany(ids)

        verify(repository, times(1)).deleteAllById(ids)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should save new PurchaseOrder`() {
        val orderCopy = orderMock.copy(id=null)
        whenever(repository.save(orderCopy)).thenReturn(this.orderMock)

        val order = this.service.create(orderCopy)

        assertThat(order.id).isNotNull.withFailMessage("Id is null")
        assertThat(order.createdAt).isNotNull.withFailMessage("createdAt is null")

        verify(repository, times(1)).save(orderCopy)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should change status PurchaseOrder`() {
        val orderCopy = orderMock.copy(status = StatusEnum.RECEIVED)
        val orderCopyAfter = orderMock.copy(status = StatusEnum.PROCESSING)

        whenever(repository.save(orderCopyAfter)).thenReturn(orderCopyAfter)

        val result = this.service.changeStatus(orderCopy, StatusEnum.PROCESSING)

        assertThat(result.id).isEqualTo(orderCopyAfter.id)
        assertThat(result.status).isEqualTo(orderCopyAfter.status)

        verify(repository, times(1)).save(orderCopyAfter)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should throw ResponseStatusException in approveOrder`() {
        val orderCopy = orderMock.copy(status = StatusEnum.CANCELLED)
        val exception = assertThrows(ResponseStatusException::class.java) {
            this.service.approveOrder(orderCopy, UUID.randomUUID().toString())
        }

        assertThat(exception.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
            .withFailMessage("Status code are different")
        assertThat(exception.reason).isEqualTo("You cannot change the status from cancelled to approved")
            .withFailMessage("Message are different")

        verifyNoInteractions(repository)
    }

    @Test
    fun `should approve order successfully`() {
        val approvedByUserId = UUID.randomUUID().toString()
        val order = orderMock.copy(status = StatusEnum.APPROVED, approvedByUserId = approvedByUserId)
        whenever(repository.save(order))
            .thenReturn(order.copy(status = StatusEnum.APPROVED, approvedByUserId = approvedByUserId))

        val result = service.approveOrder(order, approvedByUserId)

        assertThat(result.status).isEqualTo(StatusEnum.APPROVED)
            .withFailMessage("StatusEnum are different")

        assertThat(result.approvedByUserId).isEqualTo(approvedByUserId)
            .withFailMessage("approvedByUserId are different")

        assertThat(result.approveAt).isNotNull
            .withFailMessage("approveAt is null")

        verify(repository, times(1)).save(order)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should throw exception when trying to approve a cancelled order`() {
        val order = orderMock.copy(status = StatusEnum.CANCELLED)

        val ex = assertThrows<ResponseStatusException> {
            service.approveOrder(order, "user-123")
        }

        assertThat(ex.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
            .withFailMessage("statusCode are different")
        assertThat(ex.reason).isEqualTo("You cannot change the status from cancelled to approved")
            .withFailMessage("Message are different")

        verifyNoInteractions(repository)
    }

    @Test
    fun `should cancel order successfully`() {
        val canceledByUserId = "user-456"
        val reasonCancel = "out of stock"

        val order = orderMock.copy(status = StatusEnum.CANCELLED, canceledByUserId = canceledByUserId, reasonCancel = reasonCancel)
        whenever(repository.save(order)).thenReturn(order)

        val result = service.cancelOrder(order, canceledByUserId, reasonCancel)

        assertThat(result.status).isEqualTo(StatusEnum.CANCELLED)
            .withFailMessage("StatusEnum are different")

        assertThat(result.canceledByUserId).isEqualTo(canceledByUserId)
            .withFailMessage("canceledByUserId are different")

        assertThat(result.reasonCancel).isEqualTo(reasonCancel)
            .withFailMessage("reasonCancel are different")

        assertThat(result.canceledAt).isNotNull
            .withFailMessage("canceledAt are different")

        verify(repository, times(1)).save(order)
    }

    @Test
    fun `should throw exception when trying to cancel a received order`() {
        val order = orderMock.copy(status = StatusEnum.RECEIVED)

        val ex = assertThrows<ResponseStatusException> {
            service.cancelOrder(order, "user-456", "out of stock")
        }

        assertThat(ex.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
        assertThat(ex.reason).contains("received")
        verifyNoInteractions(repository)
    }

    @Test
    fun `should receive order successfully`() {
        val today = LocalDate.now()

        val order = orderMock.copy(status = StatusEnum.APPROVED, deliveryDate = today)
        whenever(repository.save(order)).thenReturn(order.copy(status = StatusEnum.RECEIVED))

        val result = service.receiveOrder(order, today)

        assertThat(result.status).isEqualTo(StatusEnum.RECEIVED)
        assertThat(result.deliveryDate).isEqualTo(today)
        verify(repository, times(1)).save(order)
    }

    @Test
    fun `should throw exception when trying to receive a cancelled order`() {
        val order = orderMock.copy(status = StatusEnum.CANCELLED)

        val ex = assertThrows<ResponseStatusException> {
            service.receiveOrder(order, LocalDate.now())
        }

        assertThat(ex.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
            .withFailMessage("statusCode are different")

        assertThat(ex.reason).contains("cancel")
            .withFailMessage("Message not contains cancel")

        verifyNoInteractions(repository)
    }
    
}