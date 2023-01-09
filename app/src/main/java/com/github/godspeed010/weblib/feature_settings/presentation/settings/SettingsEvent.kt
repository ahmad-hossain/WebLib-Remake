package com.github.godspeed010.weblib.feature_settings.presentation.settings

sealed class SettingsEvent {
    object SignInClicked : SettingsEvent()
    object SignOutClicked : SettingsEvent()
    data class ToggleAutoCloudBackup(val newValue: Boolean) : SettingsEvent()
    data class ToggleNovelsUseWebsiteTitle(val newValue: Boolean) : SettingsEvent()
}