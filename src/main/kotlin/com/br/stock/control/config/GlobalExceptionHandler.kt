package com.br.stock.control.config

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime


@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(request: HttpServletRequest,ex: AccessDeniedException?): ResponseEntity<MutableMap<String?, Any?>?> {
        val response: MutableMap<String?, Any?> = HashMap<String?, Any?>()
        response.put("timestamp", LocalDateTime.now())
        response.put("status", 403)
        response.put("error", "Forbidden")
        response.put("message", "You don't have authorization")
        response.put("path", request.requestURI)
        response.put("method", request.method)

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body<MutableMap<String?, Any?>?>(response)
    }

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatus(request: HttpServletRequest, ex: ResponseStatusException): ResponseEntity<MutableMap<String, Any?>?> {
        val response: MutableMap<String, Any?> = HashMap();

        response.put("timestamp", LocalDateTime.now())
        response.put("status", ex.statusCode)
        response.put("error", ex.reason)
        response.put("message", ex.message)
        response.put("path", request.requestURI)
        response.put("method", request.method)

        return ResponseEntity.status(ex.statusCode).body(response)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<MutableMap<String?, String?>?> {
        val errors: MutableMap<String?, String?> = HashMap<String?, String?>()

        for (error in ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage())
        }

        return ResponseEntity.badRequest().body<MutableMap<String?, String?>?>(errors)
    }

    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(ex: Exception): ResponseEntity<Any?> {
        val body: MutableMap<String?, Any?> = HashMap<String?, Any?>()
        body.put("timestamp", LocalDateTime.now())
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value())
        body.put("error", ex.cause)
        body.put("message", ex.message)

        return ResponseEntity<Any?>(body, HttpStatus.INTERNAL_SERVER_ERROR)
    }


}