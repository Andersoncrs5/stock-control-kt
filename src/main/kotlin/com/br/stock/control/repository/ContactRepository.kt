package com.br.stock.control.repository

import com.br.stock.control.model.entity.Contact
import org.springframework.data.mongodb.repository.MongoRepository

interface ContactRepository: MongoRepository<Contact, String>