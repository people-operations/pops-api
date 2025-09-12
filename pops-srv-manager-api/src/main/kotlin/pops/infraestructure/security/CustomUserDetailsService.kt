package pops.infraestructure.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import pops.domain.model.entity.Person
import pops.domain.repository.PersonRepository

@Component
class CustomUserDetailsService : UserDetailsService {
    @Autowired
    private val repository: PersonRepository? = null

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(email: String): UserDetails {
        val person: Person = repository!!.findByEmail(email)!!.get()
        return User(person.email, person.password, ArrayList())
    }
}

