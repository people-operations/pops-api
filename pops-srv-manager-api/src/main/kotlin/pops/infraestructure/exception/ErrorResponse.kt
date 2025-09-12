package pops.exception

data class ErrorResponse(
    val message: String,
    val status: Int,
    val path: String
)
