package com.maeen.mahfilhub.data.remote

import com.maeen.mahfilhub.data.model.MahfilListRequest
import com.maeen.mahfilhub.data.model.MahfilPageResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface MahfilApiService {

    @POST("mahfil/list")
    suspend fun getMahfilList(@Body request: MahfilListRequest): Response<MahfilPageResponse>
}
