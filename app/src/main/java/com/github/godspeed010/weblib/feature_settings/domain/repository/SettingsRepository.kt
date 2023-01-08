package com.github.godspeed010.weblib.feature_settings.domain.repository

import android.content.Context
import androidx.datastore.core.DataStore
import com.github.godspeed010.weblib.feature_settings.domain.model.UserPreferences

interface SettingsRepository {

    val Context.dataStore: DataStore<UserPreferences>

    suspend fun updateDatastore(pref: UserPreferences)
}