package com.github.godspeed010.weblib.feature_settings.presentation.settings

import android.content.Intent
import androidx.activity.result.IntentSenderRequest

sealed class SettingsUiEvent {
    data class Toast(val s: String) : SettingsUiEvent()
    data class LaunchOneTapIntent(val intent: IntentSenderRequest) : SettingsUiEvent()
    data class LaunchCreateDocumentIntent(val intent: Intent) : SettingsUiEvent()
}
