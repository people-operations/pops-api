package pops.domain.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pops.domain.model.entity.Project
import pops.domain.model.entity.Skill
import pops.domain.repository.ProjectRepository
import pops.domain.repository.SkillRepository
import pops.infraestructure.utilities.CrudService

@Service
class ProjectService(
    private val projectRepository: ProjectRepository,
    private val skillRepository: SkillRepository
) : CrudService<Project, Long>(projectRepository) {
    
    fun findActiveProjects(): List<Project> = projectRepository.findByActiveTrue()
    
    fun findActiveProjectsByStatus(status: pops.domain.model.enum.ProjectStatus): List<Project> = 
        projectRepository.findByActiveTrueAndStatus(status)
    
    override fun save(project: Project): Project {
        require(project.name.isNotBlank()) { "O nome do projeto não pode ser vazio" }
        
        if (projectRepository.existsByName(project.name)) {
            throw IllegalArgumentException("Já existe um projeto com o nome informado")
        }
        
        // Validar skills se fornecidas
        if (project.requiredSkills.isNotEmpty()) {
            val skillIds = project.requiredSkills.mapNotNull { it.id }
            val existingSkills = skillRepository.findAllById(skillIds)
            if (existingSkills.size != skillIds.size) {
                throw IllegalArgumentException("Uma ou mais skills informadas não existem")
            }
        }
        
        return super.save(project)
    }
    
    override fun update(id: Long, project: Project): Project {
        require(project.name.isNotBlank()) { "O nome do projeto não pode ser vazio" }
        
        val existingProject = findById(id)
        
        // Verificar se o nome já existe em outro projeto
        if (project.name != existingProject.name && projectRepository.existsByName(project.name)) {
            throw IllegalArgumentException("Já existe um projeto com o nome informado")
        }
        
        // Validar skills se fornecidas
        if (project.requiredSkills.isNotEmpty()) {
            val skillIds = project.requiredSkills.mapNotNull { it.id }
            val existingSkills = skillRepository.findAllById(skillIds)
            if (existingSkills.size != skillIds.size) {
                throw IllegalArgumentException("Uma ou mais skills informadas não existem")
            }
        }
        
        return super.update(id, project)
    }
    
    @Transactional
    fun disable(id: Long): Project {
        val project = findById(id)
        val disabledProject = project.copy(active = false)
        return projectRepository.save(disabledProject)
    }
    
    @Transactional
    fun enable(id: Long): Project {
        val project = findById(id)
        val enabledProject = project.copy(active = true)
        return projectRepository.save(enabledProject)
    }
}

