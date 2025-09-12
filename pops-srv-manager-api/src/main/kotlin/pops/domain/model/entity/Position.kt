package pops.domain.model.entity

import jakarta.persistence.Entity

@Entity(name = "job_position")
data class Position(
    @jakarta.persistence.Id
    @jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    val id: Int,
    val name: String,
    val description: String?,
    val active: Boolean = true,
)