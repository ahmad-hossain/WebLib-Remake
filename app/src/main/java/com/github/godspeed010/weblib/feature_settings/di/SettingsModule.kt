package com.github.godspeed010.weblib.feature_settings.di

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.github.godspeed010.weblib.R
import com.github.godspeed010.weblib.feature_settings.data.data_source.UserPreferencesSerializer
import com.github.godspeed010.weblib.feature_settings.data.repository.AuthRepositoryImpl
import com.github.godspeed010.weblib.feature_settings.data.repository.SettingsRepositoryImpl
import com.github.godspeed010.weblib.feature_settings.domain.model.UserPreferences
import com.github.godspeed010.weblib.feature_settings.domain.repository.AuthRepository
import com.github.godspeed010.weblib.feature_settings.domain.repository.SettingsRepository
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Named

@Module
@InstallIn(ViewModelComponent::class)
interface SettingsModule {

    @Binds
    @ViewModelScoped
    fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    companion object {
        //Names
        const val SIGN_IN_REQUEST = "signInRequest"
        const val SIGN_UP_REQUEST = "signUpRequest"

        private val Context.dataStore: DataStore<UserPreferences> by dataStore(
            fileName = "user-prefs.json",
            serializer = UserPreferencesSerializer
        )

        @Provides
        @ViewModelScoped
        fun provideDataStore(appContext: Application): DataStore<UserPreferences> {
            return appContext.dataStore
        }

        @Provides
        fun provideFirebaseAuth() = Firebase.auth

        @Provides
        fun provideFirebaseStorage() = Firebase.storage

        @Provides
        fun provideOneTapClient(
            @ApplicationContext
            context: Context
        ) = Identity.getSignInClient(context)

        @Provides
        @Named(SIGN_IN_REQUEST)
        fun provideSignInRequest(
            app: Application
        ) = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(app.getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            .setAutoSelectEnabled(true)
            .build()

        @Provides
        @Named(SIGN_UP_REQUEST)
        fun provideSignUpRequest(
            app: Application
        ) = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(app.getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            .build()

        @Provides
        fun provideGoogleSignInOptions(
            app: Application
        ) = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(app.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        @Provides
        fun provideGoogleSignInClient(
            app: Application,
            options: GoogleSignInOptions
        ) = GoogleSignIn.getClient(app, options)

        @Provides
        fun provideAuthRepository(
            auth: FirebaseAuth,
            oneTapClient: SignInClient,
            @Named(SIGN_IN_REQUEST)
            signInRequest: BeginSignInRequest,
            @Named(SIGN_UP_REQUEST)
            signUpRequest: BeginSignInRequest,
        ): AuthRepository = AuthRepositoryImpl(
            auth = auth,
            oneTapClient = oneTapClient,
            signInRequest = signInRequest,
            signUpRequest = signUpRequest,
        )
    }
}