package pops.domain.model.entity
import jakarta.persistence.Entity

@Entity(name = "career_level")
data class Level(
    @jakarta.persistence.Id
    @jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    val id: Int,
    val name: String,
    val description: String?,
    val active: Boolean = true,
)