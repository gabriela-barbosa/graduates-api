package br.uff.graduatesapi.error

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

sealed class ResponseResult<T>(
    val data: T? = null,
    val errorReason: Errors? = null,
    val errorData: String? = null,
    val errorCode: HttpStatus? = null
) {
    class Success<T>(data: T) : ResponseResult<T>(data)
    class Error<T>(errorReason: Errors?, errorData: String? = null, errorCode: HttpStatus? = null) :
        ResponseResult<T>(errorReason = errorReason, errorData = errorData, errorCode = errorCode)
}

fun ResponseResult.Error<*>.toResponseEntity(message: String? = null): ResponseEntity<Any> =
    ResponseEntity.status(this.errorCode ?: this.errorReason!!.errorCode)
        .body(message ?: this.errorReason?.responseMessage)

fun <T> ResponseResult.Error<*>.passError(errorCode: HttpStatus? = null): ResponseResult<T> =
    ResponseResult.Error(
        errorReason = this.errorReason, errorData = this.errorData, errorCode = errorCode ?: this.errorCode
    )

