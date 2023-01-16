package com.github.godspeed010.weblib.feature_settings.presentation.settings

import android.app.Activity
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.godspeed010.weblib.feature_settings.domain.model.Response
import com.github.godspeed010.weblib.feature_settings.domain.model.UserPreferences
import com.github.godspeed010.weblib.feature_settings.domain.repository.AuthRepository
import com.github.godspeed010.weblib.feature_settings.domain.repository.SettingsRepository
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    dataStore: DataStore<UserPreferences>,
    private val authRepo: AuthRepository,
    private val oneTapClient: SignInClient,
) : ViewModel() {

    var state by mutableStateOf(SettingsState())
        private set

    private val _uiEvent = MutableSharedFlow<SettingsUiEvent>()
    val uiEvent: SharedFlow<SettingsUiEvent> = _uiEvent.asSharedFlow()

    private var getDataStoreSettingsJob: Job? = null

    fun onEvent(event: SettingsEvent) {
        Timber.d("%s : %s", event::class.simpleName, event.toString())

        when (event) {
            is SettingsEvent.SignInClicked -> viewModelScope.launch {
                authRepo.oneTapSignInWithGoogle().collect {
                    when (it) {
                        is Response.Success -> {
                            state = state.copy(oneTapState = it.data)

                            val oneTapState = state.oneTapState ?: return@collect
                            val intent = IntentSenderRequest
                                .Builder(oneTapState.pendingIntent.intentSender)
                                .build()
                            _uiEvent.emit(SettingsUiEvent.LaunchOneTapIntent(intent))
                        }
                        is Response.Failure -> {
                            Timber.e(it.e, "SignInClicked Response.Failure")
                            _uiEvent.emit(SettingsUiEvent.Toast("Error: ${it.e.message}"))
                        }
                        else -> {}
                    }
                }
            }
            is SettingsEvent.SignOutClicked -> {
                oneTapClient.signOut()
                Firebase.auth.signOut()
                state = state.copy(isAuthed = false)
            }
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
            is SettingsEvent.ToggleWebViewAdblock -> {
                val data = state.settings.copy(isWebViewAdblockEnabled = event.newValue)
                viewModelScope.launch {
                    settingsRepository.updateDatastore(data)
                }
            }
            is SettingsEvent.OneTapIntentResult -> {
                if (event.result.resultCode != Activity.RESULT_OK) return

                try {
                    val credentials = oneTapClient.getSignInCredentialFromIntent(event.result.data)
                    val googleIdToken = credentials.googleIdToken
                    val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)

                    viewModelScope.launch {
                        authRepo.firebaseSignInWithGoogle(googleCredentials).collect {
                            when (it) {
                                is Response.Success -> {
                                    state = state.copy(
                                        isAuthed = true,
                                        authEmail = Firebase.auth.currentUser?.email ?: "",
                                    )
                                }
                                is Response.Failure -> {
                                    Timber.e(it.e, "OnTapIntentResult Response.Failure")
                                    _uiEvent.emit(SettingsUiEvent.Toast("Error: ${it.e.message}"))
                                }
                                else -> {}
                            }
                        }
                    }
                } catch (it: ApiException) {
                    viewModelScope.launch { _uiEvent.emit(SettingsUiEvent.Toast("Error: ${it.message}")) }
                    Timber.e(it, "OneTapIntentResult ApiException")
                }
            }
            is SettingsEvent.ExportDataClicked -> TODO()
            is SettingsEvent.ImportDataClicked -> TODO()
        }
    }

    init {
        state = state.copy(
            isAuthed = authRepo.isUserAuthenticatedInFirebase,
            authEmail = Firebase.auth.currentUser?.email ?: "",
        )

        getDataStoreSettingsJob?.cancel()
        getDataStoreSettingsJob = dataStore.data.onEach {
            state = state.copy(settings = it)
        }.launchIn(viewModelScope)
    }
}