package com.br.stock.control.config.cliRunner

import com.br.stock.control.model.entity.Role
import com.br.stock.control.util.facades.FacadeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.UUID

@Component
class RunnerRoles(
    private val facadeRepository: FacadeRepository
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        createRoleIfNotExists("ROLE_ADMIN", "Admin role")
        createRoleIfNotExists("ROLE_USER", "Default user role")
    }

    fun createRoleIfNotExists(name: String, description: String) {
        if (!facadeRepository.roleRepository.existsByName(name)) {
            facadeRepository.roleRepository.save(
                Role(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    description = description,
                    createdAt = LocalDate.now(),
                )
            )
        }
    }
}
