package pops.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import pops.domain.model.entity.Project
import pops.domain.model.enum.ProjectStatus

@Repository
interface ProjectRepository : JpaRepository<Project, Long> {
    fun findByActiveTrue(): List<Project>
    
    fun findByActiveFalse(): List<Project>
    
    @Query("SELECT p FROM project p WHERE p.active = true AND p.status = :status")
    fun findByActiveTrueAndStatus(status: ProjectStatus): List<Project>
    
    @Query("SELECT p FROM project p WHERE p.active = true AND p.status IN :statuses")
    fun findByActiveTrueAndStatusIn(statuses: List<ProjectStatus>): List<Project>
    
    fun existsByName(name: String): Boolean
    
    @Query("SELECT p FROM project p JOIN p.requiredSkills s WHERE s.id = :skillId")
    fun findProjectsBySkillId(skillId: Long): List<Project>
}


