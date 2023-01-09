package com.github.godspeed010.weblib.feature_settings.presentation.settings

import com.github.godspeed010.weblib.feature_settings.domain.model.UserPreferences

data class SettingsState(
    val settings: UserPreferences = UserPreferences(),
    val isAuthed: Boolean = false,
    val authEmail: String = "",
)