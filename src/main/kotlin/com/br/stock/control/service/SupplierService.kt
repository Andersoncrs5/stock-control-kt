package com.br.stock.control.service

import com.br.stock.control.model.dto.supplier.UpdateSupplierDTO
import com.br.stock.control.model.entity.Supplier
import com.br.stock.control.model.enum.SupplierStatusEnum
import com.br.stock.control.model.enum.SupplierTypeEnum
import com.br.stock.control.repository.SupplierRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.Optional

@Service
class SupplierService(
    private val repository: SupplierRepository
) {

    private val logger = LoggerFactory.getLogger(SupplierService::class.java)

    @Transactional(readOnly = true)
    fun get(id: String): Optional<Supplier> {
        logger.debug("Getting supplier by id $id")
        val supplier = this.repository.findById(id)
        logger.debug("Returning supplier")
        return supplier
    }

    @Transactional
    fun save(supplier: Supplier): Supplier {
        logger.debug("Saving new supplier")
        val save = this.repository.save(supplier)
        logger.debug("Supplier saved")
        return save
    }

    @Transactional
    fun delete(supplier: Supplier) {
        logger.debug("Deleting supplier")
        this.repository.delete(supplier)
        logger.debug("Supplier deleted")
    }

    @Transactional
    fun changeStatusIsPreferred(supplier: Supplier): Supplier {
        logger.debug("Changing status supplier")
        supplier.isPreferred = !supplier.isPreferred
        val save = this.repository.save(supplier)
        logger.debug("Supplier status isPreferred changed")
        return save
    }

    @Transactional(readOnly = true)
    fun existsById(id: String): Boolean {
        return this.repository.existsById(id)
    }

    @Transactional
    fun update(supplier: Supplier, dto: UpdateSupplierDTO): Supplier {
        logger.debug("Updating supplier")
        supplier.cnpj = dto.cnpj
        supplier.nameEnterprise = dto.nameEnterprise
        supplier.notes = dto.notes
        supplier.type = dto.type
        supplier.rating = dto.rating
        supplier.categoriesId = dto.categoriesId

        val save = this.repository.save(supplier)
        logger.debug("Supplier updated")
        return save
    }

    @Transactional(readOnly = true)
    fun findAll(
        userId: String?, cnpj: String?, nameEnterprise: String?, notes: String?,
        status: SupplierStatusEnum?, type: SupplierTypeEnum?,
        minRating: Int?, maxRating: Int?,
        categoriesId: List<String>?,
        createdBy: String?, isPreferred: Boolean?,
        createdAtBefore: LocalDate?, createdAtAfter: LocalDate?, pageable: Pageable
    ): Page<Supplier> {
        return this.repository.findAll(
            userId, cnpj, nameEnterprise, notes, status,
            type, minRating, maxRating, categoriesId, createdBy,
            isPreferred, createdAtBefore, createdAtAfter, pageable
        )
    }

    @Transactional(readOnly = true)
    fun existsByCnpj(cnpj: String): Boolean {
        return this.repository.existsByCnpj(cnpj)
    }

}