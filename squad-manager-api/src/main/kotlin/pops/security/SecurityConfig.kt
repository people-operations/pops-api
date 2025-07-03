package pops.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
class SecurityConfig{
    @Autowired
    private val userDetailsService: CustomUserDetailsService? = null

    @Autowired
    var securityFilter: SecurityFilter? = null

    @Autowired
    private val autenticacaoJwtEntryPoint: AutenticacaoJwtEntryPoint? = null

    val URL_PERMITIDAS = arrayOf(
        "/api/person/autenticate",
    )

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { it.disable() }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it
                    .requestMatchers(HttpMethod.POST, "/person/autenticate").permitAll()
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                    .requestMatchers(*URL_PERMITIDAS).permitAll()
                    .anyRequest().authenticated()
            }.exceptionHandling { handling ->
                handling.authenticationEntryPoint(autenticacaoJwtEntryPoint)
            }
            .sessionManagement { management ->
                management.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    @Throws(Exception::class)
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }
}