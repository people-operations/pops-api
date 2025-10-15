package pops.application.controller

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pops.domain.model.entity.Project
import pops.domain.model.enum.ProjectStatus
import pops.domain.service.ProjectService

@RestController
@RequestMapping("/projects")
class ProjectController(
    private val service: ProjectService
) {

    private val logger = LoggerFactory.getLogger(ProjectController::class.java)

    @GetMapping
    fun listActiveProjects(): ResponseEntity<Any> {
        val projects = service.findActiveProjects()
        return if (projects.isEmpty()) ResponseEntity.noContent().build()
        else ResponseEntity.ok(projects)
    }

    @GetMapping("/pageable")
    fun listProjectsPagination(@PageableDefault(size = 10) pageable: Pageable): ResponseEntity<Any> {
        val projects = service.findAllPageable(pageable)
        return if (projects.isEmpty) ResponseEntity.noContent().build()
        else ResponseEntity.ok(projects.content)
    }

    @GetMapping("/status/{status}")
    fun listProjectsByStatus(@PathVariable status: ProjectStatus): ResponseEntity<Any> {
        val projects = service.findActiveProjectsByStatus(status)
        return if (projects.isEmpty()) ResponseEntity.noContent().build()
        else ResponseEntity.ok(projects)
    }

    @GetMapping("/{id}")
    fun getProjectById(@PathVariable id: Long): ResponseEntity<Project> {
        val project = service.findById(id)
        return ResponseEntity.ok(project)
    }

    @PostMapping
    fun createProject(@RequestBody project: Project): ResponseEntity<Project> {
        val newProject = service.save(project)
        logger.info("Projeto criado com sucesso: ${newProject.id}")
        return ResponseEntity.status(201).body(newProject)
    }

    @PutMapping("/{id}")
    fun updateProject(@PathVariable id: Long, @RequestBody project: Project): ResponseEntity<Project> {
        val updatedProject = service.update(id, project)
        logger.info("Projeto atualizado com sucesso: $id")
        return ResponseEntity.ok(updatedProject)
    }

    @PutMapping("/disable/{id}")
    fun disableProject(@PathVariable id: Long): ResponseEntity<Any> {
        val disabledProject = service.disable(id)
        logger.info("Projeto desabilitado com sucesso: $id")
        return ResponseEntity.ok(disabledProject)
    }

    @PutMapping("/enable/{id}")
    fun enableProject(@PathVariable id: Long): ResponseEntity<Any> {
        val enabledProject = service.enable(id)
        logger.info("Projeto habilitado com sucesso: $id")
        return ResponseEntity.ok(enabledProject)
    }

    @DeleteMapping("/{id}")
    fun deleteProject(@PathVariable id: Long): ResponseEntity<Any> {
        service.delete(id)
        logger.info("Projeto deletado com sucesso: $id")
        return ResponseEntity.noContent().build()
    }
}

