package com.maeen.mahfilhub.data.remote

import com.maeen.mahfilhub.data.model.UserProfileResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface UserApiService {

    @GET("user/profile")
    suspend fun getProfile(
        @Header("Authorization") authHeader: String
    ): Response<UserProfileResponse>
}
