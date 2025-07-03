package pops.exception

import jakarta.persistence.EntityNotFoundException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateNameException::class)
    fun handleDuplicateName(ex: DuplicateNameException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            message = ex.message ?: "Duplicated value",
            status = HttpStatus.CONFLICT.value(),
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error)
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFound(ex: EntityNotFoundException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
            message = ex.message ?: "Resource not found",
            status = HttpStatus.NOT_FOUND.value(),
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error)
    }


    @ExceptionHandler(ReferentialIntegrityException::class)
    fun handleReferentialIntegrity(ex: ReferentialIntegrityException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val error = ErrorResponse(
                message = ex.message ?: "Linked entity cannot be deleted.",
            status = HttpStatus.CONFLICT.value(),
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error)
    }
}
