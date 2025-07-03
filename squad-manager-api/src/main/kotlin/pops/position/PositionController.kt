package pops.position

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/positions")
class PositionController(private val service: PositionService) {

    @GetMapping
    fun getAll(): ResponseEntity<List<Position>> = ResponseEntity.ok(service.findAll())

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<Position> = ResponseEntity.ok(service.findById(id))

    @PostMapping
    fun create(@RequestBody position: Position): ResponseEntity<Position> =
        ResponseEntity.ok(service.create(position))

    @PatchMapping("/{id}")
    fun partialUpdate(@PathVariable id: Long, @RequestBody updatedFields: Map<String, Any>): ResponseEntity<Position> {
        val updatedPosition = service.partialUpdate(id, updatedFields)
        return ResponseEntity.ok(updatedPosition)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}