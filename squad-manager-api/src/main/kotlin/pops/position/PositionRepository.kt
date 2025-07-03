package pops.position

import org.springframework.data.jpa.repository.JpaRepository

interface PositionRepository : JpaRepository<Position, Long> {
    fun existsByName(name: String): Boolean
}