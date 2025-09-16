package pops.domain.service

import org.springframework.stereotype.Service
import pops.domain.model.entity.Level
import pops.domain.repository.LevelRepository
import pops.infraestructure.utilities.CrudService


@Service
class LevelService (
    private val repository: LevelRepository
) : CrudService<Level>(repository)