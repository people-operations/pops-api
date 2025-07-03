package pops.level

import org.springframework.data.jpa.repository.JpaRepository

interface LevelRepository : JpaRepository<Level, Long> {
    fun existsByName(name: String): Boolean
}