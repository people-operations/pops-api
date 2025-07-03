package pops.level

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/levels")
class LevelController(private val service: LevelService) {

    @GetMapping
    fun getAll(): ResponseEntity<List<Level>> = ResponseEntity.ok(service.findAll())

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<Level> = ResponseEntity.ok(service.findById(id))

    @PostMapping
    fun create(@RequestBody level: Level): ResponseEntity<Level> =
        ResponseEntity.ok(service.create(level))

    @PatchMapping("/{id}")
    fun partialUpdate(@PathVariable id: Long, @RequestBody updatedFields: Map<String, Any>): ResponseEntity<Level> {
        val updatedLevel = service.partialUpdate(id, updatedFields)
        return ResponseEntity.ok(updatedLevel)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}