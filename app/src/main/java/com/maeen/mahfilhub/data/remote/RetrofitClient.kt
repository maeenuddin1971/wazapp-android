package com.maeen.mahfilhub.data.remote

import android.content.Context
import com.maeen.mahfilhub.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    // ── Unauthenticated client (for login/register) ─────────────────
    private val publicOkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val publicRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(publicOkHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApiService: AuthApiService = publicRetrofit.create(AuthApiService::class.java)

    // ── Authenticated client (with Bearer token) ────────────────────
    private var authenticatedRetrofit: Retrofit? = null

    /**
     * Must be called once with Application context (e.g. from Application.onCreate
     * or the first Activity) to enable authenticated API calls.
     */
    fun init(context: Context) {
        val authOkHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context.applicationContext))
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        authenticatedRetrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(authOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getAuthRetrofit(): Retrofit =
        authenticatedRetrofit ?: throw IllegalStateException(
            "RetrofitClient.init(context) must be called before using authenticated APIs"
        )

    val userApiService: UserApiService
        get() = getAuthRetrofit().create(UserApiService::class.java)

    val mahfilApiService: MahfilApiService
        get() = getAuthRetrofit().create(MahfilApiService::class.java)

    val maulanaApiService: MaulanaApiService
        get() = getAuthRetrofit().create(MaulanaApiService::class.java)
}
