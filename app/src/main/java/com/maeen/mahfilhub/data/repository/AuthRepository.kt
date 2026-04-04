package com.maeen.mahfilhub.data.repository

import com.maeen.mahfilhub.data.model.LoginRequest
import com.maeen.mahfilhub.data.model.LoginResponse
import com.maeen.mahfilhub.data.remote.RetrofitClient
import org.json.JSONObject

sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String) : Resource<Nothing>()
    data object Loading : Resource<Nothing>()
}

class AuthRepository {

    private val api = RetrofitClient.authApiService

    suspend fun login(email: String, password: String): Resource<LoginResponse> {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful) {
                response.body()?.let {
                    Resource.Success(it)
                } ?: Resource.Error("Empty response body")
            } else {
                val errorBody = response.errorBody()?.string()
                val message = parseErrorMessage(errorBody, response.code())
                Resource.Error(message)
            }
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    private fun parseErrorMessage(errorBody: String?, statusCode: Int): String {
        if (errorBody.isNullOrBlank()) return "Login failed ($statusCode)"
        return try {
            val json = JSONObject(errorBody)
            json.optString("message", "").ifBlank { "Login failed ($statusCode)" }
        } catch (_: Exception) {
            "Login failed ($statusCode)"
        }
    }
}

