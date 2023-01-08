package com.github.godspeed010.weblib.feature_settings.presentation.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

//    var state by mutableStateOf(SettingsState())
//        private set

    fun onEvent(event: SettingsEvent) {
        Timber.d("%s : %s", event::class.simpleName, event.toString())

//        when (event) {
//
//        }
    }
}