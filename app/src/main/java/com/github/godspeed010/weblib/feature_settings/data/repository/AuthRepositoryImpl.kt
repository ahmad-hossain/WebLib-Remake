package com.github.godspeed010.weblib.feature_settings.data.repository

import com.github.godspeed010.weblib.feature_settings.di.SettingsModule.Companion.SIGN_IN_REQUEST
import com.github.godspeed010.weblib.feature_settings.di.SettingsModule.Companion.SIGN_UP_REQUEST
import com.github.godspeed010.weblib.feature_settings.domain.model.Response
import com.github.godspeed010.weblib.feature_settings.domain.repository.AuthRepository
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Named

class AuthRepositoryImpl  @Inject constructor(
    private val auth: FirebaseAuth,
    private var oneTapClient: SignInClient,
    @Named(SIGN_IN_REQUEST)
    private var signInRequest: BeginSignInRequest,
    @Named(SIGN_UP_REQUEST)
    private var signUpRequest: BeginSignInRequest,
) : AuthRepository {
    override val isUserAuthenticatedInFirebase = auth.currentUser != null

    override fun oneTapSignInWithGoogle() = flow {
        try {
            emit(Response.Loading)
            val signInResult = oneTapClient.beginSignIn(signInRequest).await()
            emit(Response.Success(signInResult))
        } catch (e: Exception) {
            try {
                val signUpResult = oneTapClient.beginSignIn(signUpRequest).await()
                emit(Response.Success(signUpResult))
            } catch (e: Exception) {
                emit(Response.Failure(e))
            }
        }
    }

    override fun firebaseSignInWithGoogle(googleCredential: AuthCredential) = flow {
        try {
            emit(Response.Loading)
            auth.signInWithCredential(googleCredential).await()
            emit(Response.Success(true))
        } catch (e: Exception) {
            emit(Response.Failure(e))
        }
    }

}