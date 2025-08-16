package com.br.stock.control.service

import com.br.stock.control.model.dto.warehouse.UpdateWareDTO
import com.br.stock.control.model.entity.Warehouse
import com.br.stock.control.model.enum.WareHouseEnum
import com.br.stock.control.repository.WarehouseRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class WareHouseService(
    private val repository: WarehouseRepository,
) {
    private val logger = LoggerFactory.getLogger(WareHouseService::class.java)

    @Transactional(readOnly = true)
    fun getWareHouse(id: String): Warehouse? {
        logger.debug("Getting warehouse in database")
        val warehouse = this.repository.findById(id).orElse(null)
        logger.debug("Returning warehouse")
        return warehouse
    }

    @Transactional
    fun deleteWareHouse(ware: Warehouse) {
        logger.debug("Deleting warehouse")
        this.repository.delete(ware)
        logger.debug("Warehouse deleted")
    }

    @Transactional
    fun deleteManyWareHouse(ids: List<String>) {
        logger.debug("Deleting many warehouse...")
        this.repository.deleteAllById(ids)
        logger.debug("Deleted many warehouse!")
    }

    @Transactional
    fun save(ware: Warehouse): Warehouse {
        logger.debug("Saving new warehouse...")

        val saved = this.repository.save<Warehouse>(ware)

        logger.debug("Warehouse saved!")
        return saved
    }

    @Transactional(readOnly = true)
    fun filter(
        name: String?, addressId: String?, description: String?, responsibleUserId: String?, minAmount: Long?,
        maxAmount: Long?, minCubicMeters: Double?, maxCubicMeters: Double?, type: WareHouseEnum?, isActive: Boolean?,
        canToAdd: Boolean?, createdAtBefore: LocalDateTime?, createdAtAfter: LocalDateTime?, pageable: Pageable
    ): Page<Warehouse> {
        logger.debug("Searching warehouses....")
        val wares: Page<Warehouse> = this.repository.findWithFilters( name, addressId,description,responsibleUserId, minAmount, maxAmount,minCubicMeters, maxCubicMeters,type,isActive,canToAdd,createdAtBefore,createdAtAfter, pageable)
        logger.debug("Warehouse founded")
        return wares
    }

    @Transactional
    fun changeStatusIsActive(ware: Warehouse): Warehouse {
        logger.debug("changing status active....")
        ware.isActive = !ware.isActive
        val save = this.repository.save(ware)
        logger.debug("Status changed")
        return save
    }

    @Transactional
    fun changeStatusCanToAdd(ware: Warehouse): Warehouse {
        logger.debug("changing status canToAdd....")
        ware.canToAdd = !ware.canToAdd
        val save = this.repository.save(ware)
        logger.debug("CanToAdd changed")
        return save
    }

    @Transactional
    fun update(ware: Warehouse, dto: UpdateWareDTO): Warehouse {
        logger.debug("Updating warehouse")
        ware.name = dto.name
        ware.description = dto.description
        ware.addressId = dto.addressId
        ware.responsibleUserId = dto.responsibleUserId
        ware.amount = dto.amount
        ware.capacityCubicMeters = dto.capacityCubicMeters
        ware.type = dto.type

        val save = this.repository.save(ware)
        logger.debug("Warehouse updated")
        return save
    }

}