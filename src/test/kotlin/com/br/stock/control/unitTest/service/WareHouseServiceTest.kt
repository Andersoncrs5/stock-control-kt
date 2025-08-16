package com.br.stock.control.unitTest.service

import com.br.stock.control.model.entity.Warehouse
import com.br.stock.control.model.enum.WareHouseEnum
import com.br.stock.control.repository.WarehouseRepository
import com.br.stock.control.service.WareHouseService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.junit.jupiter.api.*
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.util.Optional
import java.util.UUID
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class WareHouseServiceTest {
    
    @Mock private lateinit var repository: WarehouseRepository
    @InjectMocks private lateinit var service: WareHouseService

    private val ware: Warehouse = Warehouse(
        id = UUID.randomUUID().toString(),
        name = "ware house 1",
        description = "ware house 1 description",
        responsibleUserId = UUID.randomUUID().toString(),
        amount = 10,
        capacityCubicMeters = 100.0,
        type = WareHouseEnum.DRY,
        isActive = true,
        canToAdd = true,
        version = 0,
        createdAt = LocalDate.now(),
        updatedAt = LocalDate.now()
    )

    @Test
    fun `should get ware`() {
        whenever(repository.findById(ware.id)).thenReturn(Optional.of(ware))

        val result = this.service.getWareHouse(ware.id)

        assertNotNull(result, "Result of service getWareHouse is null")
        assertEquals(ware.id, result.id, "Ids are different")

        verify(repository, times(1)).findById(ware.id)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should delete ware`() {
        doNothing().whenever(repository).delete(ware)

        this.service.deleteWareHouse(ware)

        verify(repository, times(1)).delete(ware)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should delete many ware`() {
        val ids: List<String> = List(10) { UUID.randomUUID().toString() }

        doNothing().whenever(repository).deleteAllById(ids)

        this.service.deleteManyWareHouse(ids)

        verify(repository, times(1)).deleteAllById(ids)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should save new house`() {
        val toSave: Warehouse = ware.copy(id = "", name = "New Warehouse to Save")
        val savedWarehouse = toSave.copy(id = UUID.randomUUID().toString())

        whenever(repository.save(any(Warehouse::class.java))).thenReturn(savedWarehouse)

        val result = this.service.save(toSave)

        assertNotNull(result, "O Warehouse salvo não deveria ser nulo")
        assertNotNull(result.id, "O ID do Warehouse salvo não deveria ser nulo")
        assertEquals(savedWarehouse.id, result.id, "O ID do warehouse retornado não corresponde ao ID salvo")
        assertEquals(toSave.name, result.name, "O nome do warehouse retornado não corresponde ao nome original")

        verify(repository, times(1)).save(any(Warehouse::class.java))
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should turn status isActive`(){
        val wareCopy = this.ware.copy(isActive = false)
        whenever(repository.save(this.ware)).thenReturn(wareCopy)

        val result = this.service.changeStatusIsActive(ware)

        assertThat(result).isNotNull
        assertThat(result.isActive).isEqualTo(wareCopy.isActive)

        verify(repository, times(1)).save(this.ware)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should turn status canToAdd`(){
        val wareCopy = this.ware.copy(canToAdd = false)
        whenever(repository.save(this.ware)).thenReturn(wareCopy)

        val result = this.service.changeStatusCanToAdd(ware)

        assertThat(result).isNotNull
        assertThat(result.canToAdd).isEqualTo(wareCopy.canToAdd)

        verify(repository, times(1)).save(this.ware)
        verifyNoMoreInteractions(repository)
    }

}