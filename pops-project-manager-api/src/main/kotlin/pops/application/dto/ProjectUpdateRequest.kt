package pops.application.dto

import pops.domain.model.enum.ProjectStatus
import java.math.BigDecimal
import java.time.LocalDate

data class ProjectUpdateRequest(
    val name: String? = null,
    val type: String? = null,
    val description: String? = null,
    val status: ProjectStatus? = null,
    val budget: BigDecimal? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val area: String? = null,
    val skillIds: List<Long>? = null
)


