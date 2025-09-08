package com.br.stock.control.unitTest.service

import com.br.stock.control.model.dto.contact.UpdateContactDTO
import com.br.stock.control.model.entity.Contact
import com.br.stock.control.repository.ContactRepository
import com.br.stock.control.service.ContactService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class ContactServiceTest {
    
    @Mock
    private lateinit var repository: ContactRepository

    @InjectMocks
    private lateinit var service: ContactService

    val contact = Contact(
        userId = "12345678",
        secondaryEmail = "naosei@example.com",
        phone = "40028922",
        secondaryPhone = "400289222",
        version = 0,
        createdAt = LocalDate.now(),
        updatedAt = LocalDate.now()
    )

    @Test
    fun `should get contact`() {
        val userId = contact.userId as String
        whenever(repository.findById(userId)).thenReturn(Optional.of(contact))

        val result = this.service.get(userId)

        assertThat(result.isPresent).isTrue
        assertThat(result.get().userId).isEqualTo(userId)

        verify(repository, times(1)).findById(userId)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should return null when get contact`() {
        val userId = contact.userId as String
        whenever(repository.findById(userId)).thenReturn(Optional.empty())

        val result = this.service.get(userId)

        assertThat(result.isEmpty).isTrue

        verify(repository, times(1)).findById(userId)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should delete contact`() {
        doNothing().whenever(repository).delete(contact)

        this.service.delete(contact)

        verify(repository, times(1)).delete(contact)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should create new contact`() {
        whenever(repository.save(contact)).thenReturn(contact)

        val save = this.service.create(contact)

        assertThat(save.userId)
            .isEqualTo(contact.userId)
            .withFailMessage("UserId are different")

        assertThat(save.secondaryEmail)
            .isEqualTo(contact.secondaryEmail)
            .withFailMessage("secondaryEmail are different")

        assertThat(save.secondaryPhone)
            .isEqualTo(contact.secondaryPhone)
            .withFailMessage("secondaryPhone are different")

        assertThat(save.phone)
            .isEqualTo(contact.phone)
            .withFailMessage("Phone are different")

        verify(repository, times(1)).save(contact)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should update contact`() {
        val dto = UpdateContactDTO(
            phone = "4002892224",
            secondaryPhone = "99999999999",
            secondaryEmail = "pochita@gmail.com"
        )

        val contactCopy = contact.copy(
            phone = dto.phone,
            secondaryPhone = dto.secondaryPhone,
            secondaryEmail = dto.secondaryEmail,
        )

        whenever(repository.save(contact)).thenReturn(contactCopy)


        val save = this.service.update(contact, dto)

        assertThat(save.userId)
            .isEqualTo(contactCopy.userId)
            .withFailMessage("UserId are different")

        assertThat(save.secondaryEmail)
            .isEqualTo(contactCopy.secondaryEmail)
            .withFailMessage("secondaryEmail are different")

        assertThat(save.secondaryPhone)
            .isEqualTo(contactCopy.secondaryPhone)
            .withFailMessage("secondaryPhone are different")

        assertThat(save.phone)
            .isEqualTo(contactCopy.phone)
            .withFailMessage("Phone are different")

        verify(repository, times(1)).save(contact)
        verifyNoMoreInteractions(repository)
    }

}