package com.maeen.mahfilhub.data.remote

import com.maeen.mahfilhub.data.model.MaulanaSignupRequest
import com.maeen.mahfilhub.data.model.MaulanaSignupResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface MaulanaApiService {

    @POST("auth/maulana/register")
    suspend fun register(@Body request: MaulanaSignupRequest): Response<MaulanaSignupResponse>
}
