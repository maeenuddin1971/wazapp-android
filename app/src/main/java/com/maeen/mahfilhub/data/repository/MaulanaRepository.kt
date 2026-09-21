package com.maeen.mahfilhub.data.repository

import android.util.Log
import com.maeen.mahfilhub.data.model.MaulanaApiItem
import com.maeen.mahfilhub.data.model.MaulanaItem
import com.maeen.mahfilhub.data.model.MaulanaListResponse
import com.maeen.mahfilhub.data.model.MaulanaSignupRequest
import com.maeen.mahfilhub.data.model.MaulanaSignupResponse
import com.maeen.mahfilhub.data.remote.RetrofitClient
import com.maeen.mahfilhub.util.Resource
import org.json.JSONObject
import kotlin.coroutines.cancellation.CancellationException

class MaulanaRepository {

    private val api get() = RetrofitClient.maulanaApiService

    /**
     * Fetch paginated list of Maolana scholars via GET /maolana/list.
     * Token is passed explicitly AND via AuthInterceptor for reliability.
     */
    suspend fun getMaulanaList(
        token: String,
        page: Int = 0,
        size: Int = 10
    ): Resource<MaulanaListResponse> {
        return try {
            Log.d(TAG, "getMaulanaList: calling API with page=$page, size=$size, token=${token.take(20)}...")
            val response = api.getMaulanaList("Bearer $token", page, size)
            Log.d(TAG, "getMaulanaList: response code=${response.code()}, isSuccessful=${response.isSuccessful}")

            if (response.isSuccessful) {
                response.body()?.let { body ->
                    Log.d(TAG, "getMaulanaList: body.message=${body.message}, items=${body.data.content.size}")
                    Resource.Success(body)
                } ?: run {
                    Log.e(TAG, "getMaulanaList: response body is null")
                    Resource.Error("Empty response body")
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "getMaulanaList: error ${response.code()} - $errorBody")
                val message = parseErrorMessage(errorBody, response.code())
                Resource.Error(message)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "getMaulanaList: exception", e)
            Resource.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    /**
     * Create a new Maulana profile via POST /maolana/create.
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
        if (errorBody.isNullOrBlank()) return "Request failed ($statusCode)"
        return try {
            val json = JSONObject(errorBody)
            json.optString("message", "").ifBlank { "Request failed ($statusCode)" }
        } catch (_: Exception) {
            "Request failed ($statusCode)"
        }
    }

    companion object {
        private const val TAG = "MaulanaRepository"

        /**
         * Map API item to domain model.
         */
        fun MaulanaApiItem.toDomain(): MaulanaItem {
            return MaulanaItem(
                id = id,
                name = fullName,
                title = specializationField ?: "Scholar",
                specialization = specializationField ?: "",
                location = "",
                totalEvents = 0,
                upcomingEvents = 0,
                followers = 0,
                rating = 0f,
                isVerified = isActive,
                isFollowing = false,
                category = "All",
                contactNumber = contactNumber,
                uniqueIdentifier = uniqueIdentifier
            )
        }
    }
}
