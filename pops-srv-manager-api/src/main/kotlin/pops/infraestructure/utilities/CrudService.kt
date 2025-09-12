package pops.infraestructure.utilities

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional

abstract class CrudService<T : Any>(
    private val repository: JpaRepository<T, Int>
) {

    open fun findById(id: Int): T =
        repository.findById(id).orElseThrow { NoSuchElementException("Entidade não encontrada") }

    open fun findAll(): List<T> = repository.findAll()

    open fun findAllPageable(pageable: Pageable): Page<T> = repository.findAll(pageable)

    open fun save(entity: T): T {
        val entityClass = entity::class
        val idProperty = entityClass.members.find { it.name == "id" }
        val idValue = idProperty?.call(entity) as? Int

        if (idValue != null && repository.existsById(idValue)) {
            throw IllegalArgumentException("Já existe uma entidade com o id informado")
        }
        return repository.save(entity)
    }

    open fun update(id: Int, entity: T): T {
        val entityClass = entity::class
        val idProperty = entityClass.members.find { it.name == "id" }
        val idValue = idProperty?.call(entity) as? Int

        if (idValue == null || idValue != id) {
            throw IllegalArgumentException("O ID da entidade não corresponde ao ID fornecido para atualização")
        }

        if (!repository.existsById(id)) {
            throw NoSuchElementException("Entidade não encontrada para atualização")
        }
        return repository.save(entity)
    }

    @Transactional
    open fun enable(id: Int) {
        val entity = findById(id)
        val entityClass = entity::class
        val activeProperty = entityClass.members.find { it.name == "active" }
        val activeValue = activeProperty?.call(entity) as? Boolean
        if (activeValue == null) {
            throw IllegalArgumentException("A entidade não possui o campo 'active'")
        }
        if (activeValue) {
            throw IllegalArgumentException("A entidade já está ativada")
        }
        val enabledEntity = entityClass.constructors.first().callBy(
            entityClass.constructors
                .first()
                .parameters
                .associateWith { param ->
                    when (param.name) {
                        "id" -> id
                        "active" -> true
                        else -> entityClass.members.find { it.name == param.name }?.call(entity)
                    }
                }
        )
        repository.save(enabledEntity)
    }

    @Transactional
    open fun disable(id: Int) {
        val entity = findById(id)
        val entityClass = entity::class
        val activeProperty = entityClass.members.find { it.name == "active" }
        val activeValue = activeProperty?.call(entity) as? Boolean
        if (activeValue == null) {
            throw IllegalArgumentException("A entidade não possui o campo 'active'")
        }
        if (!activeValue) {
            throw IllegalArgumentException("A entidade já está desativada")
        }
        val disabledEntity = entityClass.constructors.first().callBy(
            entityClass.constructors
                .first()
                .parameters
                .associateWith { param ->
                    when (param.name) {
                        "id" -> id
                        "active" -> false
                        else -> entityClass.members.find { it.name == param.name }?.call(entity)
                    }
                }
        )
        repository.save(disabledEntity)
    }

    @Transactional
    open fun delete(id: Int) {
        if (!repository.existsById(id)) {
            throw NoSuchElementException("Entidade não encontrada para exclusão")
        }
        repository.deleteById(id)
    }
}