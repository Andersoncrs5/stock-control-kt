package com.br.stock.control.config.security

import com.br.stock.control.config.security.service.TokenService
import com.br.stock.control.repository.UserRepository
import jakarta.servlet.FilterChain
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails

@Component
class SecurityFilter(
    private val tokenService: TokenService,
    private val userRepository: UserRepository
): OncePerRequestFilter() {

    override fun doFilterInternal(request: HttpServletRequest,response: HttpServletResponse,filterChain: FilterChain) {
        val token: String? = this.recoverToken(request);

        if (token != null) {
            val login: String = tokenService.validateToken(token)

            if (login.isEmpty()) {
                val user: UserDetails? = userRepository.findByEmail(login);

                if (user != null) {
                    val authentication = UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.authorities
                    )

                    SecurityContextHolder.getContext().authentication = authentication
                }

            }
        }
        filterChain.doFilter(request, response)
    }

    fun recoverToken(request: HttpServletRequest): String? {
        val authHeader: String? = request.getHeader("Authorization")
        if (authHeader.isNullOrBlank()) {
            return null
        }

        return authHeader.replace("Bearer ", "")
    }

}