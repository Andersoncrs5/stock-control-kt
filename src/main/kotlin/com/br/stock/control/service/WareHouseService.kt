package com.br.stock.control.service

import com.br.stock.control.model.entity.Warehouse
import com.br.stock.control.repository.WarehouseRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class WareHouseService(
    private val repository: WarehouseRepository,

) {

    private val logger = LoggerFactory.getLogger(ProductService::class.java)

    fun getWareHouse(id: String): Warehouse? {
        logger.debug("Getting warehouse in redis...")
        logger.debug("Ware house not found in redis! Getting warehouse in mongo db")
        val warehouse = this.repository.findById(id).orElse(null)
        logger.debug("Returning warehouse")
        return warehouse
    }



}