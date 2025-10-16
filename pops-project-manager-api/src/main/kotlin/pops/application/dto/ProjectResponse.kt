package pops.application.dto

import pops.domain.model.enum.ProjectStatus
import java.math.BigDecimal
import java.time.LocalDate

data class ProjectResponse(
    val id: Long?,
    val name: String,
    val type: String?,
    val description: String?,
    val status: ProjectStatus,
    val budget: BigDecimal?,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val area: String?,
    val active: Boolean,
    val requiredSkills: List<SkillResponse>
)


