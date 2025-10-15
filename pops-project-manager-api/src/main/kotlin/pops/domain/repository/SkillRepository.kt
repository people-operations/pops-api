package pops.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import pops.domain.model.entity.Skill

interface SkillRepository : JpaRepository<Skill, Long> {
    fun findByName(name: String): Skill?
    fun existsByName(name: String): Boolean
    fun findByActiveTrue(): List<Skill>
    
    @Query("SELECT s FROM skill s WHERE s.active = true AND s.type = :type")
    fun findByActiveTrueAndType(type: pops.domain.model.enum.SkillType): List<Skill>
}

