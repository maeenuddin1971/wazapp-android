package com.maeen.mahfilhub.data.remote

import com.maeen.mahfilhub.data.model.LoginRequest
import com.maeen.mahfilhub.data.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}

