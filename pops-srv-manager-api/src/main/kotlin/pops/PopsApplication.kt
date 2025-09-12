package pops

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PopsApplication

fun main(args: Array<String>) {
	runApplication<PopsApplication>(*args)
}
