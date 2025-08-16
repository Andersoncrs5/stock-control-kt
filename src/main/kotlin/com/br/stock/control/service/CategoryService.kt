package com.br.stock.control.service

import com.br.stock.control.model.dto.category.UpdateCategoryDTO
import com.br.stock.control.model.entity.Category
import com.br.stock.control.repository.CategoryRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.Optional

@Service
class CategoryService(
    private val repository: CategoryRepository
) {
    private val logger = LoggerFactory.getLogger(CategoryService::class.java)

    @Transactional(readOnly = true)
    fun get(id: String): Optional<Category> {
        logger.debug("Getting category by id $id")
        val category: Optional<Category> = this.repository.findById(id)
        logger.debug("Returning category")
        return category
    }

    @Transactional(readOnly = true)
    fun getAll(): List<Category> {
        logger.debug("Getting all categories...")
        val categories: List<Category> = this.repository.findAll()
        logger.debug("Returning all categories")
        return categories
    }

    @Transactional(readOnly = true)
    fun filter(
        name: String?, description: String?, active: Boolean?,
        createdAtBefore: LocalDate?, createdAtAfter: LocalDate?, pageble: Pageable
    ): Page<Category> {
        logger.debug("Getting categories filtered")
        val categories = this.repository.filter(name, description, active, createdAtBefore, createdAtAfter, pageble)
        return categories
    }

    @Transactional
    fun delete(category: Category) {
        logger.debug("Deleting category...")
        this.repository.delete(category)
        logger.debug("Category deleted")
    }

    @Transactional(readOnly = true)
    fun getByName(name: String): Optional<Category> {
        logger.debug("Getting category by name $name")
        val category: Optional<Category> = this.repository.findByName(name)
        logger.debug("Returning category search by name")
        return category
    }

    @Transactional
    fun save(category: Category): Category {
        logger.debug("Saving category...")
        val save = this.repository.save(category)
        logger.debug("Category saved")
        return save
    }

    @Transactional
    fun deleteMany(ids: List<String>) {
        logger.debug("Deleting many categories...")
        this.repository.deleteAllById(ids)
        logger.debug("Categories many deleted")
    }

    @Transactional
    fun changeStatusActive(category: Category): Category  {
        logger.debug("Changing status..")
        category.active = !category.active
        logger.debug("Saving category after changed...")
        val save = this.repository.save(category)
        logger.debug("Category saved after changed")
        return save
    }

    @Transactional
    fun update(category: Category, dto: UpdateCategoryDTO): Category {
        category.name = dto.name
        category.description = dto.description

        logger.debug("Updating category...")
        val save = this.repository.save(category)
        logger.debug("Category updated")
        return save
    }

}