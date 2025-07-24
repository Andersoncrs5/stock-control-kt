package com.br.stock.control.unitTest.service

import com.br.stock.control.model.entity.Warehouse
import com.br.stock.control.model.enum.WareHouseEnum
import com.br.stock.control.repository.WarehouseRepository
import com.br.stock.control.service.WareHouseService
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.junit.jupiter.api.*
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime
import java.util.Optional
import java.util.UUID
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class WareHouseServiceTest {
    
    @Mock
    private lateinit var repository: WarehouseRepository
    @InjectMocks
    private lateinit var service: WareHouseService

    private val ware: Warehouse = Warehouse(
        id = UUID.randomUUID().toString(),
        name = "ware house 1",
        description = "ware house 1 description",
        addressId = UUID.randomUUID().toString(),
        responsibleUserId = UUID.randomUUID().toString(),
        amount = 10,
        capacityCubicMeters = 100.0,
        type = WareHouseEnum.DRY,
        isActive = true,
        canToAdd = true,
        version = 0,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )

    @Test
    fun `should get ware`() {
        `when`(repository.findById(ware.id)).thenReturn(Optional.of(ware))

        val result = this.service.getWareHouse(ware.id)

        assertNotNull(result, "Result of service getWareHouse is null")
        assertEquals(ware.id, result.id, "Ids are different")

        verify(repository, times(1)).findById(ware.id)
    }

    @Test
    fun `should delete ware`() {
        doNothing().`when`(repository).delete(ware)

        this.service.deleteWareHouse(ware)

        verify(repository, times(1)).delete(ware)
    }

    @Test
    fun `should delete many ware`() {
        val ids: List<String> = listOf(
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
        )

        doNothing().`when`(repository).deleteAllById(ids)

        this.service.deleteManyWareHouse(ids)

        verify(repository, times(1)).deleteAllById(ids)
    }

    @Test
    fun `should save new house`() {
        val toSave: Warehouse = ware.copy(id = "", name = "New Warehouse to Save")

        val savedWarehouse = toSave.copy(id = UUID.randomUUID().toString())

        `when`(repository.save(any(Warehouse::class.java))).thenReturn(savedWarehouse)

        val result = this.service.save(toSave)

        assertNotNull(result, "O Warehouse salvo não deveria ser nulo")
        assertNotNull(result.id, "O ID do Warehouse salvo não deveria ser nulo")
        assertEquals(savedWarehouse.id, result.id, "O ID do warehouse retornado não corresponde ao ID salvo")
        assertEquals(toSave.name, result.name, "O nome do warehouse retornado não corresponde ao nome original")

        verify(repository, times(1)).save(any(Warehouse::class.java))
    }

}