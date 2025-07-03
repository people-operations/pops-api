package pops.position

import jakarta.persistence.EntityNotFoundException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import pops.exception.DuplicateNameException
import pops.exception.ReferentialIntegrityException
import pops.level.LevelRepository

@Service
class PositionService(
    private val repository: PositionRepository,
    private val positionRepository: PositionRepository,
    private val levelRepository: LevelRepository
) {

    fun findAll(): List<Position> = repository.findAll()

    fun findById(id: Long): Position = repository.findById(id)
        .orElseThrow { EntityNotFoundException("Position not found with id: $id") }

    fun create(position: Position): Position {
        if (repository.existsByName(position.name)) {
            throw DuplicateNameException("The name '${position.name}' is already in use.")
        }
        return repository.save(position)
    }

    fun partialUpdate(id: Long, updates: Map<String, Any>): Position {
        val position = findById(id)

        val updated = position.copy(
            name = (updates["name"] as? String) ?: position.name,
            description = (updates["description"] as? String?) ?: position.description,
            level = updates["level_id"]?.let {
                val levelId = (it as Number).toLong()
                levelRepository.findById(levelId)
                    .orElseThrow { EntityNotFoundException("Level not found with id $levelId") }
            } ?: position.level
        )

        return positionRepository.save(updated)
    }

    fun delete(id: Long) {
        val position = repository.findById(id)
            .orElseThrow { EntityNotFoundException("Position not found with id: $id") }

        try {
            repository.delete(position)
        } catch (ex: DataIntegrityViolationException) {
            throw ReferentialIntegrityException("It is not possible to delete the position with id $id, as it is linked to one or more people.")
        }
    }
}