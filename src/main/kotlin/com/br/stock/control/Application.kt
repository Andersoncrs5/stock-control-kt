package com.br.stock.control

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.config.EnableMongoAuditing

@SpringBootApplication
@EnableMongoAuditing
class Application

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}
