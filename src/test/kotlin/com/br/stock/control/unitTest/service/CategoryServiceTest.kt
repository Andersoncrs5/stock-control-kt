package com.br.stock.control.unitTest.service

import com.br.stock.control.model.entity.Category
import com.br.stock.control.repository.CategoryRepository
import com.br.stock.control.service.CategoryService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime
import java.util.UUID
import kotlin.random.Random
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
class CategoryServiceTest {
    @Mock
    private lateinit var repository: CategoryRepository

    @InjectMocks
    private lateinit var service: CategoryService

    val category = Category(
        id = UUID.randomUUID().toString(), name = "category ${Random.nextLong(1000)}",
        description = "Description", version = 0,
        createdAt = LocalDateTime.now(), updatedAt = LocalDateTime.now()
    )

    @Test
    fun `should get category by id`() {
        `when`(repository.findById(category.id)).thenReturn(Optional.of(category))

        val result: Optional<Category> = this.service.get(category.id)

        assertTrue(result.isPresent, "Category is empty")

        verify(repository, times(1)).findById(category.id)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should not get category by id`() {
        `when`(repository.findById(category.id)).thenReturn(Optional.empty())

        val result: Optional<Category> = this.service.get(category.id)

        assertTrue(result.isEmpty, "Category is present")

        verify(repository, times(1)).findById(category.id)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should delete category`() {
        doNothing().`when`(repository).delete(category)

        this.service.delete(category)

        verify(repository, times(1)).delete(category)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should delete many category`() {
        val ids = listOf(
            UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(),
            UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(),
        )
        doNothing().whenever(repository).deleteAllById(ids)

        this.service.deleteMany(ids)

        verify(repository, times(1)).deleteAllById(ids)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should get category by name`() {
        whenever(repository.findById(category.name)).thenReturn(Optional.of(category))

        val result: Optional<Category> = this.service.getByName(category.name)

        assertTrue(result.isPresent, "Category is empty")

        verify(repository, times(1)).findById(category.name)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should get not category by name`() {
        `when`(repository.findById(category.name)).thenReturn(Optional.empty())

        val result: Optional<Category> = this.service.getByName(category.name)

        assertTrue(result.isEmpty, "Category is present")

        verify(repository, times(1)).findById(category.name)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should change category status`() {
        val categoryCopy = category.copy(active = false)
        val categoryAfterChange = category.copy(active = true)

        `when`(repository.save(categoryCopy)).thenReturn(categoryAfterChange)

        val result = this.service.changeStatusActive(categoryCopy)

        assertEquals(result.active, categoryAfterChange.active,"Status active of category is different")
        assertEquals(result.id, categoryAfterChange.id,"Category id is different")

        verify(repository, times(1)).save(categoryCopy)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should save category`() {
        `when`(repository.save(category)).thenReturn(category)

        val save = this.service.save(category)

        assertEquals(category.id, save.id, "Ids are different")
        assertEquals(category.name, save.name, "Names are different")

        verify(repository, times(1)).save(category)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should get all categories`() {
        val list = List(8) { category.copy(UUID.randomUUID().toString()) }

        whenever(repository.findAll()).thenReturn(list)

        val result: List<Category> = this.service.getAll()

        assertThat(result)
            .hasSize(list.size)
            .containsExactlyInAnyOrderElementsOf(list)

        assertThat(result)
            .containsExactlyElementsOf(list)

        verify(repository, times(1)).findAll()
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should return empty list in get all`() {
        whenever(repository.findAll()).thenReturn(emptyList())

        val result = service.getAll()

        assertThat(result).isEmpty()

        verify(repository, times(1)).findAll()
        verifyNoMoreInteractions(repository)
    }

}