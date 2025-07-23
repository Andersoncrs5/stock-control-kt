package com.br.stock.control.unitTest.service

import com.br.stock.control.repository.WarehouseRepository
import com.br.stock.control.service.WareHouseService
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class WareHouseServiceTest {
    @Mock
    private lateinit var repository: WarehouseRepository
    @InjectMocks
    private lateinit var service: WareHouseService



}