package com.github.godspeed010.weblib.feature_settings.domain.repository

import com.github.godspeed010.weblib.feature_settings.domain.model.UserPreferences

interface SettingsRepository {

    suspend fun updateDatastore(pref: UserPreferences)
}