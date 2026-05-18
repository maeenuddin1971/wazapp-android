package com.maeen.mahfilhub.util

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.maeen.mahfilhub.R
import java.util.UUID

object GoogleSignInHelper {

    /**
     * Attempts to get a Google ID Token using Credential Manager.
     *
     * IMPORTANT: "Developer console is not setup correctly" usually means:
     * 1. The Client ID in strings.xml is NOT a "Web Application" type.
     * 2. The Android Client ID in the SAME Google Cloud project doesn't match this app's
     *    package name (com.maeen.mahfilhub) or SHA-1.
     * 3. You are using the Android Client ID instead of the Web Client ID in the code.
     */
    suspend fun getIdToken(context: Context): Result<String> {
        return try {
            val serverClientId = context.getString(R.string.google_web_client_id).trim()
            if (serverClientId.isBlank()) {
                return Result.failure(IllegalStateException("Google Web Client ID is missing in strings.xml"))
            }

            // Using GetGoogleIdOption which is standard for Credential Manager
            val googleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId(serverClientId)
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(false)
                .setNonce(UUID.randomUUID().toString())
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = CredentialManager.create(context).getCredential(
                context = context,
                request = request
            )
            val credential = result.credential

            if (credential is CustomCredential && 
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
                Result.success(googleCredential.idToken)
            } else {
                Result.failure(IllegalStateException("Unexpected credential type: ${credential.type}"))
            }
        } catch (e: GetCredentialException) {
            val message = when {
                e.message?.contains("Developer console", ignoreCase = true) == true ->
                    "Developer console setup error. Please ensure:\n" +
                    "1. Use WEB Client ID in strings.xml\n" +
                    "2. Add Android Client ID to Google Cloud Console with Package: com.maeen.mahfilhub and your SHA-1."
                else -> e.localizedMessage ?: "Sign-in failed"
            }
            Result.failure(Exception(message, e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
