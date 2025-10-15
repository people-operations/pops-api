package pops.application.dto

import pops.domain.model.enum.SkillType
import pops.domain.model.enum.ProjectStatus
import java.math.BigDecimal
import java.time.LocalDate

// Exemplos de dados para testar a API

// Skills de exemplo
val exampleSkills = listOf(
    mapOf(
        "name" to "Java",
        "description" to "Linguagem de programação Java",
        "type" to SkillType.HARD.name
    ),
    mapOf(
        "name" to "Kotlin",
        "description" to "Linguagem de programação Kotlin",
        "type" to SkillType.HARD.name
    ),
    mapOf(
        "name" to "Spring Boot",
        "description" to "Framework Spring Boot para desenvolvimento de APIs",
        "type" to SkillType.HARD.name
    ),
    mapOf(
        "name" to "Comunicação",
        "description" to "Habilidade de comunicação interpessoal",
        "type" to SkillType.SOFT.name
    ),
    mapOf(
        "name" to "Liderança",
        "description" to "Capacidade de liderar equipes",
        "type" to SkillType.SOFT.name
    )
)

// Projetos de exemplo
val exampleProjects = listOf(
    mapOf(
        "name" to "Sistema de Gestão de Projetos",
        "type" to "Desenvolvimento",
        "description" to "Sistema para gerenciar projetos e equipes",
        "status" to ProjectStatus.IN_PROGRESS.name,
        "budget" to BigDecimal("50000.00"),
        "startDate" to LocalDate.now().minusDays(30),
        "endDate" to LocalDate.now().plusDays(60),
        "area" to "Tecnologia"
    ),
    mapOf(
        "name" to "Portal do Cliente",
        "type" to "Desenvolvimento Web",
        "description" to "Portal web para clientes acessarem serviços",
        "status" to ProjectStatus.PLANNING.name,
        "budget" to BigDecimal("75000.00"),
        "startDate" to LocalDate.now().plusDays(15),
        "endDate" to LocalDate.now().plusDays(90),
        "area" to "Tecnologia"
    )
)

