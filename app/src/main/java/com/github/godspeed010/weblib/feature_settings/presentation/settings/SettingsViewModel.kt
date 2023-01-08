package com.github.godspeed010.weblib.feature_settings.presentation.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.godspeed010.weblib.feature_settings.domain.model.UserPreferences
import com.github.godspeed010.weblib.feature_settings.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    dataStore: DataStore<UserPreferences>
) : ViewModel() {

    var state by mutableStateOf(SettingsState())
        private set

    private var getDataStoreSettingsJob: Job? = null

    fun onEvent(event: SettingsEvent) {
        Timber.d("%s : %s", event::class.simpleName, event.toString())

        when (event) {
            is SettingsEvent.SignInClicked -> TODO()
            is SettingsEvent.SignOutClicked -> TODO()
            is SettingsEvent.ToggleAutoCloudBackup -> {
                val data = state.settings.copy(isAutoCloudBackupEnabled = event.newValue)
                viewModelScope.launch {
                    settingsRepository.updateDatastore(data)
                }
            }
            is SettingsEvent.ToggleNovelsUseWebsiteTitle -> {
                val data = state.settings.copy(novelsUseWebsiteTitle = event.newValue)
                viewModelScope.launch {
                    settingsRepository.updateDatastore(data)
                }
            }
        }
    }

    init {
        getDataStoreSettingsJob?.cancel()
        getDataStoreSettingsJob = dataStore.data.onEach {
            state = state.copy(settings = it)
        }.launchIn(viewModelScope)
    }
}