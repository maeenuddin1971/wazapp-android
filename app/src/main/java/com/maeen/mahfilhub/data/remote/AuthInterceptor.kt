package com.maeen.mahfilhub.data.remote

import android.content.Context
import android.util.Log
import com.maeen.mahfilhub.util.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

// OkHttp interceptor that attaches the Bearer token from
// SessionManager to every outgoing request (except auth endpoints).
class AuthInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        // Skip auth header for login/register endpoints
        val path = original.url.encodedPath
        if (path.startsWith("/auth/")) {
            return chain.proceed(original)
        }

        val token = SessionManager.getToken(context)
        Log.d(TAG, "intercept: path=$path, token=${if (token.isNullOrBlank()) "NULL/BLANK" else "present (${token.length} chars)"}")

        return if (!token.isNullOrBlank()) {
            val request = original.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
            chain.proceed(request)
        } else {
            Log.w(TAG, "intercept: No token available for path=$path, proceeding without auth")
            chain.proceed(original)
        }
    }

    companion object {
        private const val TAG = "AuthInterceptor"
    }
}
