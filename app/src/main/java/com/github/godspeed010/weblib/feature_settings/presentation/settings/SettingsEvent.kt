package com.github.godspeed010.weblib.feature_settings.presentation.settings

import androidx.activity.result.ActivityResult

sealed class SettingsEvent {
    object SignInClicked : SettingsEvent()
    object SignOutClicked : SettingsEvent()
    data class ToggleAutoCloudBackup(val newValue: Boolean) : SettingsEvent()
    data class ToggleNovelsUseWebsiteTitle(val newValue: Boolean) : SettingsEvent()
    data class ToggleWebViewAdblock(val newValue: Boolean) : SettingsEvent()
    data class OneTapIntentResult(val result: ActivityResult) : SettingsEvent()
    object ExportDataClicked : SettingsEvent()
    object ImportDataClicked : SettingsEvent()
}