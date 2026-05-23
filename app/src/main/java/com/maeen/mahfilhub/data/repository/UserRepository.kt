package com.maeen.mahfilhub.data.repository

import com.maeen.mahfilhub.data.model.UserProfileResponse
import com.maeen.mahfilhub.data.remote.RetrofitClient
import com.maeen.mahfilhub.util.Resource
import org.json.JSONObject
import kotlin.coroutines.cancellation.CancellationException

class UserRepository {

    private val api = RetrofitClient.userApiService

    suspend fun getProfile(): Resource<UserProfileResponse> {
        return try {
            val response = api.getProfile()
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
        if (errorBody.isNullOrBlank()) return "Failed to load profile ($statusCode)"
        return try {
            val json = JSONObject(errorBody)
            json.optString("message", "").ifBlank { "Failed to load profile ($statusCode)" }
        } catch (_: Exception) {
            "Failed to load profile ($statusCode)"
        }
    }
}
