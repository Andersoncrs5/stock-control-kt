package com.br.stock.control.service

import com.br.stock.control.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(): UserDetailsService {
    @Autowired lateinit var  userRepository: UserRepository

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByName(username)
            ?: throw UsernameNotFoundException("User not found with name: $username")

        return org.springframework.security.core.userdetails.User.builder()
            .username(user.name)
            .password(user.passwordHash)
            .accountExpired(!user.accountNonExpired)
            .accountLocked(!user.accountNonLocked)
            .credentialsExpired(!user.credentialsNonExpired)
            .disabled(false)
            .authorities(user.roles.map { SimpleGrantedAuthority(it.name) })
            .build()
    }
}
