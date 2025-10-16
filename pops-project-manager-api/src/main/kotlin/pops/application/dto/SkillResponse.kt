package pops.application.dto

import pops.domain.model.enum.SkillType

data class SkillResponse(
    val id: Long?,
    val name: String,
    val description: String?,
    val type: SkillType,
    val active: Boolean
)


