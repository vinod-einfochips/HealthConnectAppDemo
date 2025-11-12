package com.eic.healthconnectdemo.core.network

/**
 * Sealed class representing network operation results.
 * This can be used for future API integrations.
 */
sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()

    data class Error(val exception: Exception, val message: String? = null) : NetworkResult<Nothing>()

    object Loading : NetworkResult<Nothing>()

    val isSuccess: Boolean
        get() = this is Success

    val isError: Boolean
        get() = this is Error

    val isLoading: Boolean
        get() = this is Loading

    fun getOrNull(): T? =
        when (this) {
            is Success -> data
            else -> null
        }

    fun exceptionOrNull(): Exception? =
        when (this) {
            is Error -> exception
            else -> null
        }
}

/**
 * Execute a block and wrap the result in NetworkResult.
 */
suspend fun <T> safeApiCall(apiCall: suspend () -> T): NetworkResult<T> {
    return try {
        NetworkResult.Success(apiCall())
    } catch (e: Exception) {
        NetworkResult.Error(e, e.message)
    }
}
