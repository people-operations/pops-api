package pops.person

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PersonRepository : JpaRepository<Person, Long> {
    fun findByEmail(email: String): Optional<Person>?
}