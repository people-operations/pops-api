package pops.person

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import org.hibernate.validator.constraints.br.CPF

@Entity
@Table(name = "person")
data class Person (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false, unique = true)
    @Email(message = "Email must be valid and contain '@' and '.'")
    val email: String? = null,

    @Column(nullable = false)
    @jakarta.validation.constraints.Size(min = 6, message = "Password must be at least 6 characters long")
    val password: String? = null,

    @CPF(message = "CPF must be valid and contain 11 digits")
    @Column(nullable = false, unique = true)
    val cpf: String? = null,

    @Column(nullable = true, unique = true)
    val phone: String? = null,

    @Column(nullable = true)
    val address: String? = null
) {
}