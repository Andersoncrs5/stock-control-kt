package com.br.stock.control.unitTest.service

import com.br.stock.control.model.dto.supplier.UpdateSupplierDTO
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
import org.mockito.kotlin.doNothing

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
        categoriesId = List(5) { UUID.randomUUID().toString() } as MutableList,
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

    @Test
    fun `should save new supplier`() {
        val supplierCopy = supplierMock.copy(userId = null)
        whenever(repository.save(supplierCopy)).thenReturn(supplierMock)

        val supplier = this.service.save(supplierCopy)

        assertThat(supplier.userId).isEqualTo(supplierMock.userId).withFailMessage("User id are different")

        verify(repository, times(1)).save(supplierCopy)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should delete supplier`() {
        doNothing().whenever(repository).delete(supplierMock)

        this.service.delete(supplierMock)

        verify(repository, times(1)).delete(supplierMock)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should change status isPreference in supplier`() {
        val supplierCopy = supplierMock.copy(isPreferred = false)
        whenever(repository.save(supplierMock)).thenReturn(supplierCopy)

        val supplier = this.service.save(supplierMock)

        assertThat(supplier.userId).isEqualTo(supplierMock.userId).withFailMessage("User id are different")
        assertThat(supplier.isPreferred).isFalse.withFailMessage("isPreferred is true")

        verify(repository, times(1)).save(supplierMock)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should return true`() {
        val id = "1"
        whenever(repository.existsById(id)).thenReturn(true)

        val bool = this.service.existsById(id)

        assertThat(bool).isTrue.withFailMessage("Result is false")

        verify(repository, times(1)).existsById(id)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should update supplier`() {
        val supplierCopy = supplierMock.copy()
        val dto = UpdateSupplierDTO(
            cnpj = UUID.randomUUID().toString(),
            nameEnterprise = "lorem enterprise",
            notes = supplierMock.notes,
            type = SupplierTypeEnum.NONE,
            rating = 8,
            categoriesId = supplierMock.categoriesId
        )

        val supplierExpected = supplierCopy.copy(
            cnpj = dto.cnpj,
            nameEnterprise = dto.nameEnterprise,
            notes = dto.notes,
            type = dto.type,
            rating = dto.rating,
            categoriesId = dto.categoriesId,
        )

        whenever(repository.save(supplierCopy)).thenReturn(supplierExpected)

        val supplier: Supplier = service.update(supplierCopy, dto)

        assertThat(supplier.userId).isEqualTo(supplierExpected.userId).withFailMessage("User id are different")
        assertThat(supplier.cnpj).isEqualTo(supplierExpected.cnpj).withFailMessage("cnpj are different")
        assertThat(supplier.nameEnterprise).isEqualTo(supplierExpected.nameEnterprise).withFailMessage("nameEnterprise are different")
        assertThat(supplier.notes).isEqualTo(supplierExpected.notes).withFailMessage("notes are different")
        assertThat(supplier.status).isEqualTo(supplierExpected.status).withFailMessage("status are different")
        assertThat(supplier.type).isEqualTo(supplierExpected.type).withFailMessage("type are different")
        assertThat(supplier.rating).isEqualTo(supplierExpected.rating).withFailMessage("rating are different")
        assertThat(supplier.categoriesId).isEqualTo(supplierExpected.categoriesId).withFailMessage("categoriesId are different")

        verify(repository, times(1)).save(supplierCopy) // aqui é supplierCopy mesmo!
        verifyNoMoreInteractions(repository)
    }


}
