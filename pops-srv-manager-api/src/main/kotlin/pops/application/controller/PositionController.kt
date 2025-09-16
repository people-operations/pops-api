package pops.application.controller

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pops.domain.model.entity.Position
import pops.domain.service.PositionService

@RestController
@RequestMapping("/positions")
class PositionController (
    private val service: PositionService
){
    private val logger = LoggerFactory.getLogger(PositionService::class.java)

    @GetMapping
    fun listPositions(): ResponseEntity<Any> {
        val positions = service.findAll()
        return if (positions.isEmpty()) ResponseEntity.noContent().build()
        else ResponseEntity.ok(positions)
    }

    @GetMapping("/{id}")
    fun getPositionsById(@PathVariable id: Int): ResponseEntity<Position> {
        val position = service.findById(id)
        return ResponseEntity.ok(position)
    }

    @PostMapping("/new")
    fun createPositions(@RequestBody positions: Position): ResponseEntity<Position> {
        val newPosition = service.save(positions)
        logger.info("Posição criado com sucesso: ${newPosition.id}")
        return ResponseEntity.status(201).body(newPosition)
    }

    @PutMapping("/{id}")
    fun updatePositions(@PathVariable id: Int, @RequestBody position: Position): ResponseEntity<Position> {
        val updatedPosition = service.update(id, position)
        logger.info("Posição atualizada com sucesso: $id")
        return ResponseEntity.ok(updatedPosition)
    }

    @PutMapping("/disable/{id}")
    fun disablePositions(@PathVariable id: Int): ResponseEntity<Any> {
        val disabledPosition = service.disable(id)
        logger.info("Posição desabilitada com sucesso: $id")
        return ResponseEntity.ok(disabledPosition)
    }

    @PutMapping("/enable/{id}")
    fun enablePositions(@PathVariable id: Int): ResponseEntity<Any> {
        val enabledPosition = service.enable(id)
        logger.info("Posição habilitada com sucesso: $id")
        return ResponseEntity.ok(enabledPosition)
    }

    @DeleteMapping("/{id}")
    fun deletePositions(@PathVariable id: Int): ResponseEntity<Any> {
        service.delete(id)
        logger.info("Posição deletada com sucesso: $id")
        return ResponseEntity.noContent().build()
    }
}