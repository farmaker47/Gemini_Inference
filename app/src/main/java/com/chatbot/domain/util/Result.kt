package com.chatbot.domain.util

sealed interface DataResult<out D, out E: DataError> {
    data class Success<out D>(val data: D): DataResult<D, Nothing>
    data class Error<out E: DataError>(val error: E): DataResult<Nothing, E>
}

inline fun<T, E: DataError, R> DataResult<T, E>.map(map: (T) -> R): DataResult<R, E> {
    return when(this) {
        is DataResult.Error -> DataResult.Error(error)
        is DataResult.Success -> DataResult.Success(map(data))
    }
}

fun <T, E: DataError> DataResult<T, E>.asEmptyResult(): EmptyResult<E> {
    return map {  }
}

typealias EmptyResult<E> = DataResult<Unit, E>