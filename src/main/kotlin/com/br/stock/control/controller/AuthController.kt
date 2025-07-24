package com.br.stock.control.controller

import com.br.stock.control.util.facades.FacadeServices
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/auth")
class AuthController(
    private val facade: FacadeServices
) {



}