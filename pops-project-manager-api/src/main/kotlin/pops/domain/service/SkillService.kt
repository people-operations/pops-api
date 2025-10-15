package pops.domain.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pops.domain.model.entity.Skill
import pops.domain.repository.SkillRepository
import pops.infraestructure.utilities.CrudService

@Service
class SkillService(
    private val skillRepository: SkillRepository
) : CrudService<Skill, Long>(skillRepository) {
    
    fun findActiveSkills(): List<Skill> = skillRepository.findByActiveTrue()
    
    fun findActiveSkillsByType(type: pops.domain.model.enum.SkillType): List<Skill> = 
        skillRepository.findByActiveTrueAndType(type)
    
    override fun save(skill: Skill): Skill {
        require(skill.name.isNotBlank()) { "O nome da skill não pode ser vazio" }
        
        if (skillRepository.existsByName(skill.name)) {
            throw IllegalArgumentException("Já existe uma skill com o nome informado")
        }
        
        return super.save(skill)
    }
    
    @Transactional
    fun disable(id: Long): Skill {
        val skill = findById(id)
        val disabledSkill = skill.copy(active = false)
        return skillRepository.save(disabledSkill)
    }
    
    @Transactional
    fun enable(id: Long): Skill {
        val skill = findById(id)
        val enabledSkill = skill.copy(active = true)
        return skillRepository.save(enabledSkill)
    }
}

