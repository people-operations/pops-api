package pops.person

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import pops.security.TokenService

@Controller
@RequestMapping("/person")
class PersonController(
    private val service: PersonService,
    private val tokenService: TokenService
) {
    @GetMapping
    fun getAll(): ResponseEntity<List<Person>> = ResponseEntity.ok(service.findAll())

    @PostMapping("/autenticate")
    fun authenticate(@RequestBody PersonLoginForm: PersonLoginForm): ResponseEntity<String> {
        val person = service.authenticate(PersonLoginForm.email, PersonLoginForm.password)
            ?: return ResponseEntity.status(401).body("Authentication failed")
        return ResponseEntity.ok(tokenService.generateToken(person))
    }
}