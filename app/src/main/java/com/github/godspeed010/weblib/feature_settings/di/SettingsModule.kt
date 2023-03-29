package com.github.godspeed010.weblib.feature_settings.di

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.github.godspeed010.weblib.feature_settings.data.data_source.UserPreferencesSerializer
import com.github.godspeed010.weblib.feature_settings.data.repository.SettingsRepositoryImpl
import com.github.godspeed010.weblib.feature_settings.domain.model.UserPreferences
import com.github.godspeed010.weblib.feature_settings.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
interface SettingsModule {

    @Binds
    @ViewModelScoped
    fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    companion object {
        private val Context.dataStore: DataStore<UserPreferences> by dataStore(
            fileName = "user-prefs.json",
            serializer = UserPreferencesSerializer
        )

        @Provides
        @ViewModelScoped
        fun provideDataStore(appContext: Application): DataStore<UserPreferences> {
            return appContext.dataStore
        }
    }
}