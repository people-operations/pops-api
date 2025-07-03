package pops.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import pops.person.Person
import pops.person.PersonRepository

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

