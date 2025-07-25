package com.br.stock.control.repository

import com.br.stock.control.model.entity.Warehouse
import com.br.stock.control.util.filters.warehouse.WareHouseCustomRepository
import org.springframework.data.mongodb.repository.MongoRepository

interface WarehouseRepository: MongoRepository<Warehouse, String>, WareHouseCustomRepository {
}