package com.github.godspeed010.weblib.feature_library.presentation.folders

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.godspeed010.weblib.feature_library.domain.use_case.FolderUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoldersViewModel @Inject constructor(
    private val folderUseCases: FolderUseCases
) : ViewModel() {
    private val _foldersScreenState = mutableStateOf(FoldersState())
    val foldersScreenState: State<FoldersState> = _foldersScreenState

    fun onEvent(event: FoldersEvent) {
        when (event) {
            is FoldersEvent.AddFolder -> {
                viewModelScope.launch(Dispatchers.IO) {
                    folderUseCases.addFolder(event.folder)
                }
            }
            is FoldersEvent.DeleteFolder -> TODO()
            is FoldersEvent.RestoreFolder -> TODO()
        }
    }

    init {
        _foldersScreenState.value = foldersScreenState.value.copy(
            folders = folderUseCases.getFolders()
        )
    }
}