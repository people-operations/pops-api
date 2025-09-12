package pops.domain.model.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import pops.domain.model.enum.ContractType

@Entity(name = "person")
data class Person(
    @jakarta.persistence.Id
    @jakarta.persistence.GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    val id: Int,
    val name: String,
    val email: String,
    val password: String,
    @Column(name = "cpf", columnDefinition = "char(11)")
    val cpf: String,
    @Column(name = "cnpj", columnDefinition = "char(14)")
    val cnpj: String?,
    val linkedin: String?,
    val hiringDate: java.time.LocalDate,
    val dismissalDate: java.time.LocalDate?,
    val active: Boolean = true,
    @jakarta.persistence.Enumerated(jakarta.persistence.EnumType.STRING)
    val contractType: ContractType,
)
