package pops.level

import jakarta.persistence.EntityNotFoundException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import pops.exception.DuplicateNameException
import pops.exception.ReferentialIntegrityException

@Service
class LevelService(private val repository: LevelRepository) {

    fun findAll(): List<Level> = repository.findAll()

    fun findById(id: Long): Level = repository.findById(id)
        .orElseThrow { EntityNotFoundException("Nenhum registro encontrado com id $id") }

    fun create(level: Level): Level {
        if (repository.existsByName(level.name)) {
            throw DuplicateNameException("O nome '${level.name}' já está em uso.")
        }
        return repository.save(level)
    }

    fun partialUpdate(id: Long, updatedFields: Map<String, Any>): Level {
        val existing = findById(id)
        val updated = existing.copy(
            name = updatedFields["name"] as? String ?: existing.name,
            description = updatedFields["description"] as? String ?: existing.description
        )
        return repository.save(updated)
    }

    fun delete(id: Long) {
        val level = repository.findById(id)
            .orElseThrow { EntityNotFoundException("Nenhum nível encontrado com id $id") }

        try {
            repository.delete(level)
        } catch (ex: DataIntegrityViolationException) {
            throw ReferentialIntegrityException("Não é possível deletar o nível com id $id, pois ele está vinculado a um ou mais cargos.")
        }
    }
}