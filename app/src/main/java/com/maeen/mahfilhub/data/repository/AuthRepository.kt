package com.maeen.mahfilhub.data.repository

import com.maeen.mahfilhub.data.model.LoginRequest
import com.maeen.mahfilhub.data.model.LoginResponse
import com.maeen.mahfilhub.data.remote.RetrofitClient
import com.maeen.mahfilhub.util.Resource
import org.json.JSONObject
import kotlin.coroutines.cancellation.CancellationException

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
        } catch (e: CancellationException) {
            throw e
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

