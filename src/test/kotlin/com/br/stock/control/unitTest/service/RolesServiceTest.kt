package com.br.stock.control.unitTest.service

import com.br.stock.control.model.entity.Role
import com.br.stock.control.model.entity.User
import com.br.stock.control.repository.RoleRepository
import com.br.stock.control.repository.UserRepository
import com.br.stock.control.service.RoleService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.util.Optional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach

@ExtendWith(MockitoExtension::class)
class RolesServiceTest {
    @Mock private lateinit var repository: RoleRepository
    @Mock private lateinit var userRepository: UserRepository

    @InjectMocks private lateinit var service: RoleService

    val roleMock = Role(
        id = "2026 IS BOLSONARO",
        name = "QUE_CODIGO_IS_ESSE_BICHO",
        description = "some thing",
        createdAt = LocalDate.now()
    )

    private val roleAdmin = Role(
        id = "1",
        name = "ROLE_ADMIN",
        description = "Admin role",
        createdAt = LocalDate.now()
    )

    private val roleUser = Role(
        id = "2",
        name = "ROLE_USER",
        description = "User role",
        createdAt = LocalDate.now()
    )

    private lateinit var user: User

    @BeforeEach
    fun setup() {
        user = User(
            id = "u1",
            name = "John Doe",
            email = "john@example.com",
            passwordHash = "123",
            fullName = "John Doe",
            accountNonExpired = true,
            credentialsNonExpired = true,
            accountNonLocked = true,
            roles = mutableSetOf(),
            contact = listOf(),
            lastLoginAt = null,
            refreshToken = null,
            version = 0,
            createdAt = LocalDate.now(),
            updatedAt = LocalDate.now()
        )
    }

    @Test
    fun `should get by id`() {
        whenever(repository.findById(roleMock.id)).thenReturn(Optional.of(roleMock))

        val optional = this.service.getById(roleMock.id)

        assertThat(optional.isPresent)
            .isTrue

        assertThat(optional.get().id)
            .isEqualTo(roleMock.id)

        verify(repository, times(1)).findById(roleMock.id)
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun `should get by name`() {
        whenever(repository.findByName(roleMock.name)).thenReturn(Optional.of(roleMock))

        val optional = this.service.getById(roleMock.name)

        assertThat(optional.isPresent)
            .isTrue

        assertThat(optional.get().id)
            .isEqualTo(roleMock.id)

        assertThat(optional.get().name)
            .isEqualTo(roleMock.name)

        verify(repository, times(1)).findById(roleMock.name)
        verifyNoMoreInteractions(repository)
    }

}