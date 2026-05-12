package com.maeen.mahfilhub.util

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.maeen.mahfilhub.R

object GoogleSignInHelper {

    suspend fun getIdToken(context: Context): Result<String> {
        return try {
            val serverClientId = context.getString(R.string.google_web_client_id)
            if (serverClientId.isBlank() || serverClientId == "REPLACE_WITH_GOOGLE_WEB_CLIENT_ID") {
                return Result.failure(IllegalStateException("Missing Google web client ID"))
            }

            val googleOption = GetSignInWithGoogleOption.Builder(serverClientId)
                .build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleOption)
                .build()
            val result = CredentialManager.create(context).getCredential(
                context = context,
                request = request
            )
            val credential = result.credential

            if (
                credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
                Result.success(googleCredential.idToken)
            } else {
                Result.failure(IllegalStateException("Unexpected Google credential type"))
            }
        } catch (e: GetCredentialException) {
            Result.failure(e)
        } catch (e: GoogleIdTokenParsingException) {
            Result.failure(e)
        }
    }
}
