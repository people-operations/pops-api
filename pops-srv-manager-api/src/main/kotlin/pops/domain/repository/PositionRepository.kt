package pops.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import pops.domain.model.entity.Position

interface PositionRepository : JpaRepository<Position, Int> {
}