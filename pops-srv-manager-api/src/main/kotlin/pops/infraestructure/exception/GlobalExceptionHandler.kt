package pops.exception

import jakarta.persistence.EntityNotFoundException
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(DuplicateMemberException::class)
    fun handleDuplicateName(ex: DuplicateMemberException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        logger.warn("DuplicateNameException: ${ex.message}")
        val error = ErrorResponse(
            message = ex.message ?: "Duplicated value",
            status = HttpStatus.CONFLICT.value(),
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error)
    }

    @ExceptionHandler(EntityNotFoundException::class, NoSuchElementException::class)
    fun handleNotFound(ex: Exception, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        logger.warn("NotFoundException: ${ex.message}")
        val error = ErrorResponse(
            message = ex.message ?: "Resource not found",
            status = HttpStatus.NOT_FOUND.value(),
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error)
    }

    @ExceptionHandler(ReferentialIntegrityException::class, DataIntegrityViolationException::class)
    fun handleReferentialIntegrity(ex: Exception, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        logger.warn("ReferentialIntegrityException: ${ex.message}")
        val error = ErrorResponse(
            message = ex.message ?: "Linked entity cannot be deleted.",
            status = HttpStatus.CONFLICT.value(),
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(ex: IllegalArgumentException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        logger.warn("IllegalArgumentException: ${ex.message}")
        val error = ErrorResponse(
            message = ex.message ?: "Bad request",
            status = HttpStatus.BAD_REQUEST.value(),
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        logger.error("InternalServerError: ${ex.message}", ex)
        val error = ErrorResponse(
            message = "Erro interno no servidor",
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error)
    }
}
