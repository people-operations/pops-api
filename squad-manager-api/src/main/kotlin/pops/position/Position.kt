package pops.position

import jakarta.persistence.*
import pops.level.Level

@Entity
@Table(name = "positions")
data class Position(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val name: String,

    @Column(nullable = true)
    val description: String? = null,

    @ManyToOne
    @JoinColumn(name = "level_id", nullable = false)
    val level: Level
)