package pops.domain.service

import org.springframework.stereotype.Service
import pops.domain.model.entity.Person
import pops.domain.repository.PersonRepository
import pops.exception.DuplicateMemberException
import pops.infraestructure.utilities.CrudService

@Service
class PersonService(
    private val repository: PersonRepository,
    private val levelService: LevelService,
    private val positionService: PositionService
) : CrudService<Person>(repository) {
    fun authenticate(email: String, password: String): Person? {
        return repository.findByEmail(email)?.takeIf { it.get().password == password }?.orElseThrow()
    }

    override fun save(person: Person): Person {
        require(person.email.isNotBlank()) { "O email não pode ser vazio" }
        require(person.cpf.isNotBlank()) { "O cpf não pode ser vazio" }

        if (repository.existsByEmail(person.email)) throw DuplicateMemberException("Já existe uma pessoa com o email informado")
        if (repository.existsByCpf(person.cpf)) throw DuplicateMemberException("Já existe uma pessoa com o CPF informado")
        person.cnpj?.let {
            if (repository.existsByCnpj(person.cnpj!!)) throw DuplicateMemberException("Já existe uma pessoa com o CNPJ informado")
        }

        return super.save(person)
    }
}