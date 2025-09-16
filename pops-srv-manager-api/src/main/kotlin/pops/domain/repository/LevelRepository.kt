package pops.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import pops.domain.model.entity.Level

interface LevelRepository: JpaRepository<Level, Int> {
}