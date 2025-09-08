package com.br.stock.control.config.cliRunner

import com.br.stock.control.model.entity.Role
import com.br.stock.control.model.entity.User
import com.br.stock.control.util.facades.FacadeRepository
import com.br.stock.control.util.facades.FacadeServices
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.Random
import java.util.UUID

@Component
class SuperAdmRunner(
    private val facadeServices: FacadeServices,
    private val facadeRepository: FacadeRepository
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        createSuperAdm()
    }

    fun createSuperAdm() {
        val role = createRoleIfNotExists(
            name = "ROLE_SUPER_ADMIN",
            description = "Super administrator role"
        )

        val email = "admin@gmail.com"
        if (facadeRepository.userRepository.existsByEmail(email)) {
            return
        }

        val passwordPlain = Random().nextLong(10000000).toString()
        val passwordEncoded = facadeServices.cryptoService.encoderPassword(passwordPlain)

        val user = User(
            id = UUID.randomUUID().toString(),
            name = "SuperAdmin",
            email = email,
            passwordHash = passwordEncoded,
            fullName = "super admin",
            accountNonExpired = true,
            credentialsNonExpired = true,
            accountNonLocked = true,
            roles = setOf(role),
            contact = listOf(),
            lastLoginAt = null,
            refreshToken = null,
            version = 0,
            createdAt = LocalDate.now(),
            updatedAt = LocalDate.now()
        )

        facadeRepository.userRepository.save(user)

        println("\nSuper admin created with successfully!")
        println("Name: ${user.name}")
        println("Email: $email")
        println("Password: $passwordPlain\n")
    }

    private fun createRoleIfNotExists(name: String, description: String): Role {
        val repo = facadeRepository.roleRepository
        return (if (repo.existsByName(name)) {
            repo.findByName(name).get()
        } else {
            val role = Role(
                id = UUID.randomUUID().toString(),
                name = name,
                description = description,
                createdAt = LocalDate.now(),
            )
            repo.save(role)
        })
    }
}
