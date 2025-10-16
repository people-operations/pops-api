package pops.application.dto

import pops.domain.model.enum.SkillType

data class SkillCreateRequest(
    val name: String,
    val description: String?,
    val type: SkillType
)


