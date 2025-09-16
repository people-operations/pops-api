package pops.domain.service

import org.springframework.stereotype.Service
import pops.domain.model.entity.Position
import pops.domain.repository.PositionRepository
import pops.infraestructure.utilities.CrudService

@Service
class PositionService (
    private val repository: PositionRepository
) : CrudService<Position>(repository)
