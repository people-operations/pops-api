package pops.application.controller

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pops.application.dto.PersonLoginForm
import pops.domain.model.entity.Person
import pops.domain.service.PersonService
import pops.infraestructure.security.TokenService

@RestController
@RequestMapping("/persons")
class PersonController(
    private val service: PersonService,
    private val tokenService: TokenService
) {

    private val logger = LoggerFactory.getLogger(PersonController::class.java)

    @PostMapping("/authenticate")
    fun authenticate(@RequestBody personLoginForm: PersonLoginForm): ResponseEntity<Any> {
        logger.info("Tentativa de autenticação para o email: ${personLoginForm.email}")
        val person = service.authenticate(personLoginForm.email, personLoginForm.password)
            ?: return ResponseEntity.status(401)
                .body(mapOf("error" to "Falha na autenticação: email ou senha inválidos"))
        val token = tokenService.generateToken(person)
        return ResponseEntity.ok(mapOf("token" to token))

    }

    @GetMapping
    fun listPersons(): ResponseEntity<Any> {
        val persons = service.findAll()
        return if (persons.isEmpty()) ResponseEntity.noContent().build()
        else ResponseEntity.ok(persons)
    }

    @GetMapping("/pageable")
    fun listPersonsPagination(@PageableDefault(size = 5) pageable: Pageable): ResponseEntity<Any> {
        val persons = service.findAllPageable(pageable)
        return if (persons.isEmpty) ResponseEntity.noContent().build()
        else ResponseEntity.ok(persons.content)
    }

    @GetMapping("/{id}")
    fun getPersonById(@PathVariable id: Int): ResponseEntity<Person> {
        val person = service.findById(id)
        return ResponseEntity.ok(person)
    }

    @PostMapping("/new")
    fun createPerson(@RequestBody person: Person): ResponseEntity<Person> {
        val newPerson = service.save(person)
        logger.info("Pessoa criada com sucesso: ${newPerson.id}")
        return ResponseEntity.status(201).body(newPerson)
    }

    @PutMapping("/{id}")
    fun updatePerson(@PathVariable id: Int, @RequestBody person: Person): ResponseEntity<Person> {
        val updatedPerson = service.update(id, person)
        logger.info("Pessoa atualizada com sucesso: $id")
        return ResponseEntity.ok(updatedPerson)
    }

    @PutMapping("/disable/{id}")
    fun disablePerson(@PathVariable id: Int): ResponseEntity<Any> {
        val disabledPerson = service.disable(id)
        logger.info("Pessoa desabilitada com sucesso: $id")
        return ResponseEntity.ok(disabledPerson)
    }

    @PutMapping("/enable/{id}")
    fun enablePerson(@PathVariable id: Int): ResponseEntity<Any> {
        val enabledPerson = service.enable(id)
        logger.info("Pessoa habilitada com sucesso: $id")
        return ResponseEntity.ok(enabledPerson)
    }

    @DeleteMapping("/{id}")
    fun deletePerson(@PathVariable id: Int): ResponseEntity<Any> {
        service.delete(id)
        logger.info("Pessoa deletada com sucesso: $id")
        return ResponseEntity.noContent().build()
    }
}
