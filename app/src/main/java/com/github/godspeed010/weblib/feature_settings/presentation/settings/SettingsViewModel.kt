package com.github.godspeed010.weblib.feature_settings.presentation.settings

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.godspeed010.weblib.feature_library.data.data_source.LibraryDatabase
import com.github.godspeed010.weblib.feature_library.domain.repository.LibraryRepository
import com.github.godspeed010.weblib.feature_settings.domain.model.Response
import com.github.godspeed010.weblib.feature_settings.domain.model.UserPreferences
import com.github.godspeed010.weblib.feature_settings.domain.repository.AuthRepository
import com.github.godspeed010.weblib.feature_settings.domain.repository.SettingsRepository
import com.github.godspeed010.weblib.feature_settings.domain.use_case.ValidSqlLiteDb
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.system.exitProcess


@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    dataStore: DataStore<UserPreferences>,
    private val authRepo: AuthRepository,
    private val oneTapClient: SignInClient,
    private val app: Application,
    private val libraryRepo: LibraryRepository,
    private val validSqlLiteDbUseCase: ValidSqlLiteDb,
    private val db: LibraryDatabase,
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
                updateDataStore(data)
            }
            is SettingsEvent.ToggleNovelsUseWebsiteTitle -> {
                val data = state.settings.copy(novelsUseWebsiteTitle = event.newValue)
                updateDataStore(data)
            }
            is SettingsEvent.ToggleWebViewAdblock -> {
                val data = state.settings.copy(isWebViewAdblockEnabled = event.newValue)
                updateDataStore(data)
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
            is SettingsEvent.ExportDataClicked -> {
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    type = "*/*"
                    putExtra(Intent.EXTRA_TITLE, "weblib_backup.db")
                }
                viewModelScope.launch {
                    _uiEvent.emit(SettingsUiEvent.LaunchCreateDocumentIntent(intent))
                }
            }
            is SettingsEvent.ImportDataClicked -> {
                val dbMimeTypes = arrayOf("application/vnd.sqlite3", "application/octet-stream")
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "*/*"
                    putExtra(Intent.EXTRA_MIME_TYPES, dbMimeTypes)
                }
                viewModelScope.launch {
                    _uiEvent.emit(SettingsUiEvent.LaunchOpenDocumentIntent(intent))
                }
            }
            is SettingsEvent.OnCreateDocumentActivityResult -> {
                if (event.result.resultCode != Activity.RESULT_OK) return
                val userChosenUri = event.result.data?.data
                if (userChosenUri == null) {
                    viewModelScope.launch { _uiEvent.emit(SettingsUiEvent.Toast("Error: Uri is null")) }
                    return
                }

                viewModelScope.launch(Dispatchers.IO) {
                    libraryRepo.checkpoint()

                    val inputStream = app.getDatabasePath(LibraryDatabase.DATABASE_NAME).inputStream()
                    val outputStream = app.contentResolver.openOutputStream(userChosenUri) ?: return@launch

                    inputStream.copyTo(outputStream)

                    inputStream.close()
                    outputStream.close()
                }
            }
            is SettingsEvent.OnOpenDocumentActivityResult -> {
                if (event.result.resultCode != Activity.RESULT_OK) return
                val dbUri = event.result.data?.data ?: return
                if (!validSqlLiteDbUseCase.isValid(dbUri)) {
                    Timber.d("OnOpenDocumentActivityResult: Invalid sqlite db")
                    viewModelScope.launch { _uiEvent.emit(SettingsUiEvent.Toast("Error: Invalid file")) }
                    return
                }

                viewModelScope.launch(Dispatchers.IO) {
                    db.close()

                    val inputStream = app.contentResolver.openInputStream(dbUri) ?: return@launch
                    val outputStream = app.getDatabasePath(LibraryDatabase.DATABASE_NAME).outputStream()

                    inputStream.copyTo(outputStream)
                    inputStream.close()
                    outputStream.close()

                    val intent = app.packageManager.getLaunchIntentForPackage(app.packageName)
                    intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    app.startActivity(intent)
                    exitProcess(0)
                }
            }
        }
    }

    private fun updateDataStore(newData: UserPreferences) {
        viewModelScope.launch { settingsRepository.updateDatastore(newData) }
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