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
import java.time.LocalDateTime

@Document(collection = "users")
data class User  (
    @Id
    var id: String,
    @Indexed(unique = true)
    var name: String,
    @Indexed(unique = true)
    var email: String,
    var passwordHash: String,
    var fullName: String,
    var accountNonExpired: Boolean = true,
    var credentialsNonExpired: Boolean = true,
    var accountNonLocked: Boolean = true,
    var roles: Set<Role> = emptySet(),
    var addressId: String?,
    var contact: Contact?,
    var lastLoginAt: LocalDateTime? = null,
    var refreshToken: String?,
    @Version
    var version: Long = 0,
    @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @LastModifiedDate
    var updatedAt: LocalDateTime = LocalDateTime.now()
): UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return roles.map { role -> SimpleGrantedAuthority(role.name) }
    }

    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return username
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
