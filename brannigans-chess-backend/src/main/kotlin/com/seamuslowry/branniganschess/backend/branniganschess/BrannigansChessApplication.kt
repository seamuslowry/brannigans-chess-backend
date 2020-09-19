package com.seamuslowry.branniganschess.backend.branniganschess

import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BrannigansChessApplication

fun main(args: Array<String>) {
	runApplication<BrannigansChessApplication>(*args)
}
