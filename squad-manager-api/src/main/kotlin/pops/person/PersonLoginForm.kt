package pops.person

import jakarta.persistence.Column
import jakarta.validation.constraints.Email

data class PersonLoginForm(
    @Column(nullable = false)
    @Email(message = "Email must be valid and contain '@' and '.'")
    val email: String,

    @Column(nullable = false)
    @jakarta.validation.constraints.Size(min = 6, message = "Password must be at least 6 characters long")
    val password: String
) {
}