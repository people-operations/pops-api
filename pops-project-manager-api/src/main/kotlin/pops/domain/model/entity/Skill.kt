package pops.domain.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Enumerated
import jakarta.persistence.EnumType
import pops.domain.model.enum.SkillType

@Entity(name = "skill")
data class Skill(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(nullable = false, length = 100)
    val name: String,
    
    @Column(length = 500)
    val description: String?,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: SkillType,
    
    @Column(nullable = false)
    val active: Boolean = true
)


