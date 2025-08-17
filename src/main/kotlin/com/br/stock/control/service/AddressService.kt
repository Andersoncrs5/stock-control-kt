package com.br.stock.control.service

import com.br.stock.control.model.entity.Address
import com.br.stock.control.model.enum.TypeAddressEnum
import com.br.stock.control.repository.AddressRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Optional

@Service
class AddressService(
    private val repository: AddressRepository
) {
    private val logger = LoggerFactory.getLogger(AddressService::class.java)

    @Transactional(readOnly = true)
    fun get(id: String): Optional<Address> {
        logger.debug("Getting address by id")
        val opt: Optional<Address> = this.repository.findById(id)
        logger.debug("Returning address")
        return opt
    }

    @Transactional
    fun delete(address: Address) {
        logger.debug("Deleting address")
        this.repository.delete(address)
        logger.debug("Address deleted")
    }

    @Transactional
    fun deleteMany(ids: List<String>) {
        logger.debug("Deleting many address")
        this.repository.deleteAllById(ids)
        logger.debug("Address many deleted")
    }

    @Transactional
    fun save(address: Address): Address {
        logger.debug("Saving address")
        val save: Address = this.repository.save(address)
        logger.debug("Address save")
        return save
    }

    fun mergeAddress(original: Address, toMerge: Address): Address {
        logger.debug("Merging addresses...")

        original.street = toMerge.street
        original.number = toMerge.number
        original.complement = toMerge.complement
        original.neighborhood = toMerge.neighborhood
        original.city = toMerge.city
        original.state = toMerge.state
        original.zipCode = toMerge.zipCode
        original.country = toMerge.country
        original.referencePoint = toMerge.referencePoint
        original.latitude = toMerge.latitude
        original.longitude = toMerge.longitude
        original.type = toMerge.type

        logger.debug("Addresses merged")
        return original
    }

    @Transactional
    fun update(original: Address, toMerge: Address): Address {
        logger.debug("Updating address")
        val address = this.save(
            this.mergeAddress(original, toMerge)
        )

        logger.debug("Address updated")

        return address
    }

    @Transactional
    fun changeStatusActive(address: Address): Address {
        logger.debug("Change status address active...")
        address.isActive = !address.isActive
        val save = this.repository.save(address)
        logger.debug("Address active changed")
        return save
    }

    @Transactional(readOnly = true)
    fun existsByIdAndType(id: String, type: TypeAddressEnum): Boolean {
        logger.debug("Checking if exists a address with id and type")
        return this.repository.existsByIdAndType(id, type)
    }

}