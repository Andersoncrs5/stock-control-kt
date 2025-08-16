package com.br.stock.control.util.responses

import java.time.LocalDateTime

data class ResponseBody<T>(
    val timestamp: LocalDateTime,
    val message: String?,
    val path: String?,
    val method: String?,
    val body: T
) {
}