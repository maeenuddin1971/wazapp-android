package com.maeen.mahfilhub.data.repository

import com.maeen.mahfilhub.data.model.MaulanaSignupRequest
import com.maeen.mahfilhub.data.model.MaulanaSignupResponse
import com.maeen.mahfilhub.data.remote.RetrofitClient
import com.maeen.mahfilhub.util.Resource
import org.json.JSONObject
import kotlin.coroutines.cancellation.CancellationException

class MaulanaRepository {

    private val api get() = RetrofitClient.maulanaApiService

    /**
     * Create a new Maulana profile via POST /maolana/create.
     * Uses the authenticated Retrofit client (JWT Bearer token attached
     * automatically by AuthInterceptor).
     */
    suspend fun createMaulana(request: MaulanaSignupRequest): Resource<MaulanaSignupResponse> {
        return try {
            val response = api.createMaulana(request)
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
        if (errorBody.isNullOrBlank()) return "Maulana creation failed ($statusCode)"
        return try {
            val json = JSONObject(errorBody)
            json.optString("message", "").ifBlank { "Maulana creation failed ($statusCode)" }
        } catch (_: Exception) {
            "Maulana creation failed ($statusCode)"
        }
    }
}
