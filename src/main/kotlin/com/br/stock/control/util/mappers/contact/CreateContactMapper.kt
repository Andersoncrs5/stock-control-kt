package com.br.stock.control.util.mappers.contact

import com.br.stock.control.model.dto.contact.CreateContactDTO
import com.br.stock.control.model.entity.Contact
import com.github.dozermapper.core.Mapper
import org.springframework.stereotype.Service

@Service
class CreateContactMapper(
    private val mapper: Mapper
) {
    fun toDTO(contact: Contact): CreateContactDTO {
        return this.mapper.map(contact, CreateContactDTO::class.java)
    }

    fun toContact(dto: CreateContactDTO): Contact {
        return this.mapper.map(dto, Contact::class.java)
    }
}