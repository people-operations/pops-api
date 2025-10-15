package pops.application.controller

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pops.domain.model.entity.Skill
import pops.domain.model.enum.SkillType
import pops.domain.service.SkillService

@RestController
@RequestMapping("/skills")
class SkillController(
    private val service: SkillService
) {

    private val logger = LoggerFactory.getLogger(SkillController::class.java)

    @GetMapping
    fun listSkills(): ResponseEntity<Any> {
        val skills = service.findActiveSkills()
        return if (skills.isEmpty()) ResponseEntity.noContent().build()
        else ResponseEntity.ok(skills)
    }

    @GetMapping("/pageable")
    fun listSkillsPagination(@PageableDefault(size = 10) pageable: Pageable): ResponseEntity<Any> {
        val skills = service.findAllPageable(pageable)
        return if (skills.isEmpty) ResponseEntity.noContent().build()
        else ResponseEntity.ok(skills.content)
    }

    @GetMapping("/type/{type}")
    fun listSkillsByType(@PathVariable type: SkillType): ResponseEntity<Any> {
        val skills = service.findActiveSkillsByType(type)
        return if (skills.isEmpty()) ResponseEntity.noContent().build()
        else ResponseEntity.ok(skills)
    }

    @GetMapping("/{id}")
    fun getSkillById(@PathVariable id: Long): ResponseEntity<Skill> {
        val skill = service.findById(id)
        return ResponseEntity.ok(skill)
    }

    @PostMapping
    fun createSkill(@RequestBody skill: Skill): ResponseEntity<Skill> {
        val newSkill = service.save(skill)
        logger.info("Skill criada com sucesso: ${newSkill.id}")
        return ResponseEntity.status(201).body(newSkill)
    }

    @PutMapping("/{id}")
    fun updateSkill(@PathVariable id: Long, @RequestBody skill: Skill): ResponseEntity<Skill> {
        val updatedSkill = service.update(id, skill)
        logger.info("Skill atualizada com sucesso: $id")
        return ResponseEntity.ok(updatedSkill)
    }

    @PutMapping("/disable/{id}")
    fun disableSkill(@PathVariable id: Long): ResponseEntity<Any> {
        val disabledSkill = service.disable(id)
        logger.info("Skill desabilitada com sucesso: $id")
        return ResponseEntity.ok(disabledSkill)
    }

    @PutMapping("/enable/{id}")
    fun enableSkill(@PathVariable id: Long): ResponseEntity<Any> {
        val enabledSkill = service.enable(id)
        logger.info("Skill habilitada com sucesso: $id")
        return ResponseEntity.ok(enabledSkill)
    }

    @DeleteMapping("/{id}")
    fun deleteSkill(@PathVariable id: Long): ResponseEntity<Any> {
        service.delete(id)
        logger.info("Skill deletada com sucesso: $id")
        return ResponseEntity.noContent().build()
    }
}

