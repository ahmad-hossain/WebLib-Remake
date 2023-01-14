package com.github.godspeed010.weblib.feature_settings.presentation.settings

import com.github.godspeed010.weblib.feature_settings.domain.model.UserPreferences
import com.google.android.gms.auth.api.identity.BeginSignInResult

data class SettingsState(
    val settings: UserPreferences = UserPreferences(),
    val isAuthed: Boolean = false,
    val authEmail: String = "",
    val oneTapState: BeginSignInResult? = null,
)