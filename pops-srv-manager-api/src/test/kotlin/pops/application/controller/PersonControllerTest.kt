import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.springframework.http.HttpStatus
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import pops.application.controller.PersonController
import pops.application.dto.PersonLoginForm
import pops.domain.model.entity.Person
import pops.domain.model.enum.ContractType
import pops.domain.service.PersonService
import pops.infraestructure.security.TokenService
import java.time.LocalDate

class PersonControllerTest {

    private val service = Mockito.mock(PersonService::class.java)
    private val tokenService = Mockito.mock(TokenService::class.java)
    private val controller = PersonController(service, tokenService)

    private fun buildPerson(id: Int, name: String, email: String): Person {
        return Person(
            id = id,
            name = name,
            email = email,
            password = "password123",
            cpf = "1234567890$id",
            cnpj = null,
            linkedin = null,
            hiringDate = LocalDate.now(),
            dismissalDate = null,
            active = true,
            contractType = ContractType.CLT
        )
    }

    @Test
    fun shouldAuthenticateWithValidCredentials() {
        val login = PersonLoginForm("miguel@email.com", "password123")
        val person = buildPerson(1, "Miguel", login.email)

        `when`(service.authenticate(login.email, login.password)).thenReturn(person)
        `when`(tokenService.generateToken(person)).thenReturn("fake-jwt-token")

        val response = controller.authenticate(login)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("fake-jwt-token", (response.body as Map<*, *>)["token"])
    }

    @Test
    fun shouldNotAuthenticateWithInvalidCredentials() {
        val login = PersonLoginForm("wrong@email.com", "wrongpass")

        `when`(service.authenticate(login.email, login.password)).thenReturn(null)

        val response = controller.authenticate(login)

        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)
        assertEquals("Falha na autenticação: email ou senha inválidos", (response.body as Map<*, *>)["error"])
    }

    @Test
    fun shouldListPersonsSuccessfully() {
        val persons = listOf(buildPerson(1, "Miguel", "miguel@email.com"))
        `when`(service.findAll()).thenReturn(persons)

        val response = controller.listPersons()

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(1, (response.body as List<*>).size)
    }

    @Test
    fun shouldReturnNoContentWhenNoPersons() {
        `when`(service.findAll()).thenReturn(emptyList())

        val response = controller.listPersons()

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }

    @Test
    fun shouldReturnPagedPersons() {
        val pageable = PageRequest.of(0, 5)
        val persons = listOf(buildPerson(1, "Miguel", "miguel@email.com"))
        val page = PageImpl(persons, pageable, persons.size.toLong())

        `when`(service.findAllPageable(pageable)).thenReturn(page)

        val response = controller.listPersonsPagination(pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(1, (response.body as List<*>).size)
    }

    @Test
    fun shouldReturnNoContentWhenPagedPersonsEmpty() {
        val pageable = PageRequest.of(0, 5)
        val page = PageImpl(emptyList<Person>(), pageable, 0)

        `when`(service.findAllPageable(pageable)).thenReturn(page)

        val response = controller.listPersonsPagination(pageable)

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
    }

    @Test
    fun shouldGetPersonById() {
        val person = buildPerson(1, "Miguel", "miguel@email.com")

        `when`(service.findById(1)).thenReturn(person)

        val response = controller.getPersonById(1)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("Miguel", response.body?.name)
    }

    @Test
    fun shouldCreatePersonSuccessfully() {
        val person = buildPerson(1, "Novo", "novo@email.com")

        `when`(service.save(person)).thenReturn(person)

        val response = controller.createPerson(person)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals("Novo", response.body?.name)
    }

    @Test
    fun shouldUpdatePersonSuccessfully() {
        val person = buildPerson(1, "Atualizado", "update@email.com")

        `when`(service.update(1, person)).thenReturn(person)

        val response = controller.updatePerson(1, person)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals("Atualizado", response.body?.name)
    }

    @Test
    fun shouldDisablePersonSuccessfully() {
        val person = buildPerson(1, "Desabilitado", "disable@email.com")

        Mockito.doNothing().`when`(service).disable(1)

        val response = controller.disablePerson(1)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(Unit, response.body)
    }

    @Test
    fun shouldEnablePersonSuccessfully() {
        val person = buildPerson(1, "Habilitado", "enable@email.com")

        Mockito.doNothing().`when`(service).enable(1)

        val response = controller.enablePerson(1)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(Unit, response.body)
    }

    @Test
    fun shouldDeletePersonSuccessfully() {
        val response = controller.deletePerson(1)

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
        Mockito.verify(service).delete(1)
    }
}
