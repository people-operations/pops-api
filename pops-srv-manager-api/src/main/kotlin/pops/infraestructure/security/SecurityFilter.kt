package pops.infraestructure.security

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import pops.domain.model.entity.Person
import pops.domain.repository.PersonRepository
import java.io.IOException

@Component
class SecurityFilter : OncePerRequestFilter() {
    @Autowired
    var tokenService: TokenService? = null

    @Autowired
    var personRepository: PersonRepository? = null

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val path = request.servletPath
        if (path == "/api/persons/authenticate" || path == "/api/health" || path == "/api/actuator/health") {
            filterChain.doFilter(request, response)
            return
        }

        val token = this.recoverToken(request)
        val login = tokenService!!.validateToken(token)

        if (login != null) {
            val person: Person = personRepository!!.findByEmail(login)?.get() ?: throw IllegalArgumentException("User not found");
            val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
            val authentication = UsernamePasswordAuthenticationToken(person, null, authorities)
            SecurityContextHolder.getContext().authentication = authentication
        }
        filterChain.doFilter(request, response)
    }

    private fun recoverToken(request: HttpServletRequest): String? {
        val authHeader = request.getHeader("Authorization") ?: return null
        return authHeader.replace("Bearer ", "")
    }
}