package pops.application.dto

import pops.domain.model.enum.SkillType

data class SkillUpdateRequest(
    val name: String? = null,
    val description: String? = null,
    val type: SkillType? = null
)


