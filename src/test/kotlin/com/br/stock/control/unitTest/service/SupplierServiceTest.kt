package com.br.stock.control.unitTest.service

import com.br.stock.control.model.entity.Supplier
import com.br.stock.control.model.enum.SupplierStatusEnum
import com.br.stock.control.model.enum.SupplierTypeEnum
import com.br.stock.control.repository.SupplierRepository
import com.br.stock.control.service.SupplierService
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
import java.util.UUID
import org.assertj.core.api.Assertions.assertThat

@ExtendWith(MockitoExtension::class)
class SupplierServiceTest {

    @Mock
    private lateinit var repository: SupplierRepository

    @InjectMocks
    private lateinit var service: SupplierService

    private val supplierMock = Supplier(
        userId = UUID.randomUUID().toString(),
        cnpj = UUID.randomUUID().toString(),
        nameEnterprise = "lorem",
        notes = "12345678 lorem",
        status = SupplierStatusEnum.ACTIVE,
        type = SupplierTypeEnum.MEI,
        rating = 10,
        categoriesId = List(5) { UUID.randomUUID().toString() },
        createdBy = UUID.randomUUID().toString(),
        isPreferred = true,
        version = 0,
        createdAt = LocalDate.now(),
        updatedAt = LocalDate.now()
    )

    @Test
    fun `should get supplier`() {
        whenever(repository.findById(supplierMock.userId!!))
            .thenReturn(Optional.of(supplierMock))

        val result = service.get(supplierMock.userId!!)

        assertThat(result.isPresent)
            .withFailMessage("Supplier not found")
            .isTrue()

        assertThat(result.get().userId)
            .isEqualTo(supplierMock.userId)

        verify(repository, times(1)).findById(supplierMock.userId!!)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should not get supplier`() {
        whenever(repository.findById(supplierMock.userId!!))
            .thenReturn(Optional.empty())

        val result = service.get(supplierMock.userId!!)

        assertThat(result.isEmpty)
            .withFailMessage("Supplier not found")
            .isTrue()

        verify(repository, times(1)).findById(supplierMock.userId!!)
        verifyNoMoreInteractions(repository)
    }

}
