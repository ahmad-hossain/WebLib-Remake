package com.github.godspeed010.weblib.feature_settings.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.github.godspeed010.weblib.feature_settings.data.data_source.UserPreferencesSerializer
import com.github.godspeed010.weblib.feature_settings.domain.model.UserPreferences
import com.github.godspeed010.weblib.feature_settings.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : SettingsRepository {
    override val Context.dataStore: DataStore<UserPreferences> by dataStore(
        fileName = "user-prefs.json",
        serializer = UserPreferencesSerializer
    )

    override suspend fun updateDatastore(pref: UserPreferences) {
        context.dataStore.updateData { pref }
    }
}