package pops.person

import org.springframework.stereotype.Service

@Service
class PersonService(
    private val repository: PersonRepository
) {
    fun findAll(): List<Person> = repository.findAll()

    fun authenticate(email: String, password: String): Person? {
        return repository.findByEmail(email)?.takeIf { it.get().password.equals(password) }!!.orElseThrow()
    }
}