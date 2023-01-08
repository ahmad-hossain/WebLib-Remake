package com.github.godspeed010.weblib.feature_settings.di

import com.github.godspeed010.weblib.feature_settings.data.repository.SettingsRepositoryImpl
import com.github.godspeed010.weblib.feature_settings.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
interface SettingsModule {

    @Binds
    @Singleton
    fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}