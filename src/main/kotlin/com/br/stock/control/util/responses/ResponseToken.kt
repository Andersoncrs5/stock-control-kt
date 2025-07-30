package com.br.stock.control.util.responses

import java.time.LocalDateTime

data class ResponseToken(
    val token: String,
    val refreshToken: String,
    val expireAtToken: LocalDateTime,
    val expireAtRefreshToken: LocalDateTime
) {
}