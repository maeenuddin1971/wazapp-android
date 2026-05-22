package com.maeen.mahfilhub.data.repository

import com.maeen.mahfilhub.data.model.MaulanaSignupRequest
import com.maeen.mahfilhub.data.model.MaulanaSignupResponse
import com.maeen.mahfilhub.data.remote.RetrofitClient
import com.maeen.mahfilhub.util.Resource
import org.json.JSONObject
import kotlin.coroutines.cancellation.CancellationException

class MaulanaRepository {

    private val api = RetrofitClient.maulanaApiService

    suspend fun register(request: MaulanaSignupRequest): Resource<MaulanaSignupResponse> {
        return try {
            val response = api.register(request)
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
        if (errorBody.isNullOrBlank()) return "Registration failed ($statusCode)"
        return try {
            val json = JSONObject(errorBody)
            json.optString("message", "").ifBlank { "Registration failed ($statusCode)" }
        } catch (_: Exception) {
            "Registration failed ($statusCode)"
        }
    }
}
