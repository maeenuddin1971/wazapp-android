package com.maeen.mahfilhub.data.repository

import com.maeen.mahfilhub.data.model.MahfilListRequest
import com.maeen.mahfilhub.data.model.MahfilPageResponse
import com.maeen.mahfilhub.data.remote.RetrofitClient
import com.maeen.mahfilhub.util.Resource
import org.json.JSONObject
import kotlin.coroutines.cancellation.CancellationException

class MahfilRepository {

    private val api = RetrofitClient.mahfilApiService

    suspend fun getMahfilList(request: MahfilListRequest): Resource<MahfilPageResponse> {
        return try {
            val response = api.getMahfilList(request)
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
        if (errorBody.isNullOrBlank()) return "Failed to load mahfils ($statusCode)"
        return try {
            val json = JSONObject(errorBody)
            json.optString("message", "").ifBlank { "Failed to load mahfils ($statusCode)" }
        } catch (_: Exception) {
            "Failed to load mahfils ($statusCode)"
        }
    }
}
