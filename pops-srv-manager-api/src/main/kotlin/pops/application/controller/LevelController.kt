package pops.application.controller

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pops.domain.model.entity.Level
import pops.domain.service.LevelService

@RestController
@RequestMapping("/levels")
class LevelController (
    private val service: LevelService
){
    private val logger = LoggerFactory.getLogger(LevelController::class.java)
    
    @GetMapping
    fun listLevels(): ResponseEntity<Any> {
        val levels = service.findAll()
        return if (levels.isEmpty()) ResponseEntity.noContent().build()
        else ResponseEntity.ok(levels)
    }

    @GetMapping("/{id}")
    fun getLevelById(@PathVariable id: Int): ResponseEntity<Level> {
        val level = service.findById(id)
        return ResponseEntity.ok(level)
    }

    @PostMapping("/new")
    fun createLevel(@RequestBody level: Level): ResponseEntity<Level> {
        val newLevel = service.save(level)
        logger.info("Level criado com sucesso: ${newLevel.id}")
        return ResponseEntity.status(201).body(newLevel)
    }

    @PutMapping("/{id}")
    fun updateLevel(@PathVariable id: Int, @RequestBody level: Level): ResponseEntity<Level> {
        val updatedLevel = service.update(id, level)
        logger.info("Level atualizado com sucesso: $id")
        return ResponseEntity.ok(updatedLevel)
    }

    @PutMapping("/disable/{id}")
    fun disableLevel(@PathVariable id: Int): ResponseEntity<Any> {
        val disabledLevel = service.disable(id)
        logger.info("Level desabilitado com sucesso: $id")
        return ResponseEntity.ok(disabledLevel)
    }

    @PutMapping("/enable/{id}")
    fun enableLevel(@PathVariable id: Int): ResponseEntity<Any> {
        val enabledLevel = service.enable(id)
        logger.info("Level habilitado com sucesso: $id")
        return ResponseEntity.ok(enabledLevel)
    }

    @DeleteMapping("/{id}")
    fun deleteLevel(@PathVariable id: Int): ResponseEntity<Any> {
        service.delete(id)
        logger.info("Level deletado com sucesso: $id")
        return ResponseEntity.noContent().build()
    }
}