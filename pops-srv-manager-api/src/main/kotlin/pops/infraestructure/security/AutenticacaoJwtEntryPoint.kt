package pops.infraestructure.security

import jakarta.servlet.http.*
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.InsufficientAuthenticationException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class AutenticacaoJwtEntryPoint : AuthenticationEntryPoint {

    @Throws(IOException::class)
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        when {
            authException is BadCredentialsException || authException is InsufficientAuthenticationException -> {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
            }
            // Exemplo para HTTP 400 (Bad Request)
            request.method == "POST" && request.requestURI.contains("someBadRequestPath") -> {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request parameters")
            }
            // Exemplo para HTTP 404 (Not Found)
            request.requestURI.contains("nonexistentResource") -> {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource not found")
            }
            // Exemplo para HTTP 204 (No Content)
            request.method == "GET" && request.requestURI.contains("someNoContentPath") -> {
                response.status = HttpServletResponse.SC_NO_CONTENT
            }
            else -> {
                response.sendError(HttpServletResponse.SC_FORBIDDEN)
            }
        }
    }
}

