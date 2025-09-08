package com.br.stock.control.service

import com.br.stock.control.model.dto.contact.UpdateContactDTO
import com.br.stock.control.model.entity.Contact
import com.br.stock.control.repository.ContactRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Optional

@Service
class ContactService(
    private val repository: ContactRepository
) {
    private val logger = LoggerFactory.getLogger(ContactService::class.java)

    @Transactional(readOnly = true)
    fun get(id: String): Optional<Contact> {
        logger.debug("Getting contact by Id: $id")
        return this.repository.findById(id)
    }

    @Transactional
    fun delete(contact: Contact) {
        logger.debug("Deleting contact by entity: ${contact.userId}")
        this.repository.delete(contact)
    }

    @Transactional
    fun deleteById(userId: String) {
        logger.debug("Deleting contact by Id: $userId")
        this.repository.deleteById(userId)
    }

    @Transactional
    fun create(contact: Contact): Contact {
        logger.debug("Creating contact")
        val contactSaved = this.repository.save(contact)
        logger.debug("Contact created")
        return contactSaved
    }

    @Transactional
    fun update(contact: Contact, dto: UpdateContactDTO): Contact {
        logger.debug("Updating contact")

        contact.phone = dto.phone
        contact.secondaryEmail = dto.secondaryEmail
        contact.secondaryPhone = dto.secondaryPhone

        val contactSaved = this.repository.save(contact)
        logger.debug("Contact updated")
        return contactSaved
    }

}