package com.github.godspeed010.weblib.feature_settings.domain.repository

import com.github.godspeed010.weblib.feature_settings.domain.model.Response
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.firebase.auth.AuthCredential
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val isUserAuthenticatedInFirebase: Boolean

    fun oneTapSignInWithGoogle(): Flow<Response<BeginSignInResult>>

    fun firebaseSignInWithGoogle(googleCredential: AuthCredential): Flow<Response<Boolean>>
}