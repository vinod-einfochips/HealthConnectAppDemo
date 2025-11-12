package com.eic.healthconnectdemo.domain.model

/**
 * A sealed class representing the result of an operation.
 * Used for type-safe error handling without exceptions.
 */
sealed class Result<out T> {
    /**
     * Represents a successful operation with data.
     */
    data class Success<T>(val data: T) : Result<T>()

    /**
     * Represents a failed operation with an exception.
     */
    data class Error(val exception: Exception) : Result<Nothing>()

    /**
     * Maps the success value to a new type.
     */
    inline fun <R> map(transform: (T) -> R): Result<R> {
        return when (this) {
            is Success -> Success(transform(data))
            is Error -> this
        }
    }

    /**
     * Executes the given action if this is a Success.
     */
    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }

    /**
     * Executes the given action if this is an Error.
     */
    inline fun onError(action: (Exception) -> Unit): Result<T> {
        if (this is Error) action(exception)
        return this
    }

    /**
     * Returns the data if Success, null if Error.
     */
    fun getOrNull(): T? =
        when (this) {
            is Success -> data
            is Error -> null
        }

    /**
     * Returns the data if Success, throws exception if Error.
     */
    fun getOrThrow(): T =
        when (this) {
            is Success -> data
            is Error -> throw exception
        }
}
