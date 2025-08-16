package com.br.stock.control.model.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDate

@Document(collection = "users")
data class User  (
    @Id
    var id: String = "",
    @Indexed(unique = true)
    var name: String = "",
    @Indexed(unique = true)
    var email: String = "",
    var passwordHash: String = "",
    var fullName: String = "",
    var accountNonExpired: Boolean = false,
    var credentialsNonExpired: Boolean = false,
    var accountNonLocked: Boolean = false,
    var roles: Set<Role> = emptySet(),
    var contact: List<String> = listOf(),
    var lastLoginAt: LocalDate? = null,
    var refreshToken: String? = null,
    @Version
    var version: Long = 0,
    @CreatedDate
    var createdAt: LocalDate = LocalDate.now(),
    @LastModifiedDate
    var updatedAt: LocalDate = LocalDate.now()
): UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return roles.map { role -> SimpleGrantedAuthority(role.name) }
    }

    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return name
    }

    override fun isAccountNonExpired(): Boolean {
        return accountNonExpired
    }

    override fun isAccountNonLocked(): Boolean {
        return accountNonLocked
    }

    override fun isCredentialsNonExpired(): Boolean {
        return credentialsNonExpired
    }

    override fun isEnabled(): Boolean {
        return isEnabled
    }
}
