package com.br.stock.control.unitTest.service

import com.br.stock.control.model.entity.Address
import com.br.stock.control.model.enum.TypeAddressEnum
import com.br.stock.control.repository.AddressRepository
import com.br.stock.control.service.AddressService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.assertj.core.api.Assertions.assertThat
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.util.Optional
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class AddressServiceTest {
    @Mock
    private lateinit var repository: AddressRepository

    @InjectMocks
    private lateinit var service: AddressService

    val addressMock = Address(
        id = UUID.randomUUID().toString(),
        street = "street 1",
        number = "1",
        complement = "complement",
        neighborhood = "neighborhood 1",
        city = "city 1",
        state = "state 1",
        zipCode = "12345678",
        country = "country 1",
        referencePoint = "referencePoint 1",
        latitude = 84566.24,
        longitude = 983578.224,
        isActive = true,
        type = TypeAddressEnum.USER,
        version = 1,
        createdAt = LocalDate.now(),
        updatedAt = LocalDate.now()
    )

    @Test
    fun `should return null where get address`() {
        whenever(repository.findById(addressMock.id)).thenReturn(Optional.empty())

        val result = this.service.get(addressMock.id)

        assertThat(result.isEmpty).isTrue

        verify(repository, times(1)).findById(addressMock.id)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should get address`() {
        whenever(repository.findById(addressMock.id)).thenReturn(Optional.of(addressMock))

        val result = this.service.get(addressMock.id)

        assertThat(result.isPresent).isTrue
        assertThat(result.get().id).isEqualTo(addressMock.id)

        verify(repository, times(1)).findById(addressMock.id)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should delete address`() {
        doNothing().whenever(repository).delete(addressMock)

        this.service.delete(addressMock)

        verify(repository, times(1)).delete(addressMock)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should save a address`() {
        val copy = this.addressMock.copy()
        whenever(repository.save(copy)).thenReturn(copy)

        val result: Address = this.service.save(copy)

        assertThat(result).isNotNull
        assertThat(result.id).isEqualTo(result.id)
        assertThat(result.street).isEqualTo(result.street)

        verify(repository, times(1)).save(copy)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should delete many address`() {
        val ids: List<String> = List(10) { UUID.randomUUID().toString() }
        doNothing().whenever(repository).deleteAllById(ids)

        this.service.deleteMany(ids)

        verify(repository, times(1)).deleteAllById(ids)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should merge address`() {
        val addressToMerge = Address(
            id = this.addressMock.id, street = "street 1" + 1, number = "1" + 1, complement = "complement" + 1,
            neighborhood = "neighborhood 1" + 1, city = "city 1" + 1,
            state = "state 1" + 1, zipCode = "12345678" + 9, country = "country 1" + 1,
            referencePoint = "referencePoint 1" + 1, latitude = 84566.24 + 1.1, longitude = 983578.224 + 1.1, isActive = true,
            type = TypeAddressEnum.USER, version = this.addressMock.version, createdAt = this.addressMock.createdAt, updatedAt = LocalDate.now()
        )

        val result = this.service.mergeAddress(this.addressMock, addressToMerge)

        assertThat(result).isNotNull
        assertThat(result.id).isEqualTo(addressToMerge.id)
        assertThat(result.street).isEqualTo(addressToMerge.street)
        assertThat(result.number).isEqualTo(addressToMerge.number)
        assertThat(result.complement).isEqualTo(addressToMerge.complement)
        assertThat(result.neighborhood).isEqualTo(addressToMerge.neighborhood)
        assertThat(result.city).isEqualTo(addressToMerge.city)
        assertThat(result.state).isEqualTo(addressToMerge.state)
        assertThat(result.zipCode).isEqualTo(addressToMerge.zipCode)
        assertThat(result.country).isEqualTo(addressToMerge.country)
        assertThat(result.type).isEqualTo(addressToMerge.type)
        assertThat(result.createdAt).isEqualTo(addressToMerge.createdAt)
    }

    @Test
    fun `should change status active`() {
        val copy = this.addressMock.copy(isActive = false)
        val copyChanged = this.addressMock.copy(isActive = true)

        whenever(repository.save(copy)).thenReturn(copyChanged)

        val result: Address = this.service.save(copy)

        assertThat(result).isNotNull
        assertThat(result.id).isEqualTo(result.id)
        assertThat(result.isActive).isEqualTo(result.isActive)

        verify(repository, times(1)).save(copy)
        verifyNoMoreInteractions(repository)
    }

}