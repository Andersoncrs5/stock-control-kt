package com.br.stock.control.util.facades

import com.br.stock.control.repository.AddressRepository
import com.br.stock.control.repository.CategoryRepository
import com.br.stock.control.repository.ContactRepository
import com.br.stock.control.repository.ProductRepository
import com.br.stock.control.repository.PurchaseOrderItemRepository
import com.br.stock.control.repository.PurchaseOrderRepository
import com.br.stock.control.repository.RoleRepository
import com.br.stock.control.repository.StockMovementRepository
import com.br.stock.control.repository.StockRepository
import com.br.stock.control.repository.SupplierRepository
import com.br.stock.control.repository.UserRepository
import com.br.stock.control.repository.WarehouseRepository
import org.springframework.stereotype.Service

@Service
class FacadeRepository(
    val userRepository: UserRepository,
    val productRepository: ProductRepository,
    val categoryRepository: CategoryRepository,
    val wareHouseRepository: WarehouseRepository,
    val addressRepository: AddressRepository,
    val stockRepository: StockRepository,
    val stockMovementRepository: StockMovementRepository,
    val supplierRepository: SupplierRepository,
    val purchaseOrderRepository: PurchaseOrderRepository,
    val purchaseOrderItemRepository: PurchaseOrderItemRepository,
    val contactRepository: ContactRepository,
    val roleRepository: RoleRepository
)