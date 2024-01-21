package br.uff.graduatesapi.error

sealed class ResponseResult<T>(
    val data: T? = null,
    val errorReason: Errors? = null
) {
    class Success<T>(data: T) : ResponseResult<T>(data)
    class Error<T>(errorReason: Errors?, data: T? = null) : ResponseResult<T>(data, errorReason)
}
