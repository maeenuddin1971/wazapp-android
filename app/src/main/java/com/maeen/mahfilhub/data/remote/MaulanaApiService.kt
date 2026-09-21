package com.maeen.mahfilhub.data.remote

import com.maeen.mahfilhub.data.model.MaulanaListResponse
import com.maeen.mahfilhub.data.model.MaulanaSignupRequest
import com.maeen.mahfilhub.data.model.MaulanaSignupResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface MaulanaApiService {

    /**
     * Create a new Maulana profile.
     * Requires JWT Bearer token (handled by AuthInterceptor).
     */
    @POST("maolana/create")
    suspend fun createMaulana(@Body request: MaulanaSignupRequest): Response<MaulanaSignupResponse>

    /**
     * Fetch paginated list of Maolana scholars.
     */
    @GET("maolana/list")
    suspend fun getMaulanaList(
        @Header("Authorization") authHeader: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Response<MaulanaListResponse>
}
