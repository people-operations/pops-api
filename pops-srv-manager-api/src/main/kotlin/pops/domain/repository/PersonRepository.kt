package pops.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import pops.domain.model.entity.Person
import java.util.*

interface PersonRepository: JpaRepository<Person, Int> {
    fun findByEmail(email: String): Optional<Person>?
    fun existsByEmail(email: String): Boolean
    fun existsByCpf(cpf: String): Boolean
    fun existsByCnpj(cnpj: String): Boolean
}