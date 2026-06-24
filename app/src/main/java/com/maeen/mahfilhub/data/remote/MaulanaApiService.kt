package com.maeen.mahfilhub.data.remote

import com.maeen.mahfilhub.data.model.MaulanaSignupRequest
import com.maeen.mahfilhub.data.model.MaulanaSignupResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface MaulanaApiService {

    /**
     * Create a new Maulana profile.
     * Requires JWT Bearer token (handled by AuthInterceptor).
     */
    @POST("maolana/create")
    suspend fun createMaulana(@Body request: MaulanaSignupRequest): Response<MaulanaSignupResponse>
}
