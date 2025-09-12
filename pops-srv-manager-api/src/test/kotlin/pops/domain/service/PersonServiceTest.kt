import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import pops.domain.model.entity.Person
import pops.domain.model.enum.ContractType
import pops.domain.service.PersonService
import pops.exception.DuplicateMemberException
import java.util.*

class PersonServiceTest {
    val repository = org.mockito.Mockito.mock(pops.domain.repository.PersonRepository::class.java)
    val service = PersonService(repository)

    var person1 = Person(
        id = 1,
        name = "Miguel",
        email = "miguel@email.com",
        password = "password123",
        cpf = "12345678901",
        cnpj = "12345678000199",
        linkedin = null,
        hiringDate = java.time.LocalDate.now(),
        dismissalDate = null,
        active = true,
        contractType = ContractType.CLT
    )

    @Test
    fun shouldCreatePersonSuccessfully() {
        val repository = org.mockito.Mockito.mock(pops.domain.repository.PersonRepository::class.java)
        val service = PersonService(repository)

        org.mockito.Mockito.`when`(repository.save(person1)).thenReturn(person1)
        val saved = service.save(person1)
        assertEquals("Miguel", saved.name)
        assertEquals("miguel@email.com", saved.email)
    }

    @Test
    fun shouldNotCreatePersonWithDuplicateEmail() {
        val person2 = Person(
            id = 2,
            name = "Ana",
            email = "miguel@email.com",
            password = "password123",
            cpf = "12345678901",
            cnpj = null,
            linkedin = null,
            hiringDate = java.time.LocalDate.now(),
            dismissalDate = null,
            active = true,
            contractType = ContractType.CLT
        )

        org.mockito.Mockito.`when`(repository.existsByEmail("miguel@email.com")).thenReturn(false).thenReturn(true)
        org.mockito.Mockito.`when`(repository.save(person1)).thenReturn(person1)

        service.save(person1)
        val exception = assertThrows<DuplicateMemberException> {
            service.save(person2)
        }
        assertEquals("Já existe uma pessoa com o email informado", exception.message)
    }

    @Test
    fun shouldNotCreatePersonWithDuplicateCpf() {
        val person2 = Person(
            id = 2,
            name = "Ana",
            email = "ana@email.com",
            password = "password123",
            cpf = "12345678901",
            cnpj = null,
            linkedin = null,
            hiringDate = java.time.LocalDate.now(),
            dismissalDate = null,
            active = true,
            contractType = ContractType.CLT
        )

        org.mockito.Mockito.`when`(repository.existsByCpf("12345678901")).thenReturn(false).thenReturn(true)
        org.mockito.Mockito.`when`(repository.save(person1)).thenReturn(person1)

        service.save(person1)
        val exception = assertThrows<DuplicateMemberException> {
            service.save(person2)
        }
        assertEquals("Já existe uma pessoa com o CPF informado", exception.message)
    }

    @Test
    fun shouldNotCreatePersonWithDuplicateCnpj() {
        val person2 = Person(
            id = 2,
            name = "Ana",
            email = "ana@email.com",
            password = "password123",
            cpf = "12345678901",
            cnpj = "12345678000199",
            linkedin = null,
            hiringDate = java.time.LocalDate.now(),
            dismissalDate = null,
            active = true,
            contractType = ContractType.CLT
        )
        org.mockito.Mockito.`when`(repository.existsByCnpj("12345678000199")).thenReturn(false).thenReturn(true)
        org.mockito.Mockito.`when`(repository.save(person1)).thenReturn(person1)


        service.save(person1)
        val exception = assertThrows<DuplicateMemberException> {
            service.save(person2)
        }
        assertEquals("Já existe uma pessoa com o CNPJ informado", exception.message)
    }

    @Test
    fun shouldNotCreatePersonWithInvalidCpf() {
        val person = Person(
            id = 2,
            name = "Ana",
            email = "ana@email.com",
            password = "password123",
            cpf = "",
            cnpj = "12345678000199",
            linkedin = null,
            hiringDate = java.time.LocalDate.now(),
            dismissalDate = null,
            active = true,
            contractType = ContractType.CLT
        )
        val exception = assertThrows<IllegalArgumentException> {
            service.save(person)
        }

        assertEquals("O cpf não pode ser vazio", exception.message)
    }

    @Test
    fun shouldNotCreatePersonWithInvalidEmail() {
        val person = Person(
            id = 2,
            name = "Ana",
            email = "",
            password = "password123",
            cpf = "12345678901",
            cnpj = "12345678000199",
            linkedin = null,
            hiringDate = java.time.LocalDate.now(),
            dismissalDate = null,
            active = true,
            contractType = ContractType.CLT
        )
        val exception = assertThrows<IllegalArgumentException> {
            service.save(person)
        }

        assertEquals("O email não pode ser vazio", exception.message)
    }

    @Test
    fun shouldAuthenticateWithValidCredentials() {
        org.mockito.Mockito.`when`(repository.findByEmail("miguel@email.com")).thenReturn(Optional.of(person1))
        val authenticated = service.authenticate("miguel@email.com", "password123")
        assertEquals(person1, authenticated)
    }

    @Test
    fun shouldThrowExceptionWithInvalidCredentials() {
        org.mockito.Mockito.`when`(repository.findByEmail("miguel@email.com")).thenReturn(Optional.empty())
        assertThrows<NoSuchElementException> {
            service.authenticate("miguel@email.com", "wrongpassword")
        }
    }
}
