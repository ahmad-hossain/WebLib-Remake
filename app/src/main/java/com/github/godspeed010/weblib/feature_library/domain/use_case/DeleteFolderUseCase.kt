package com.github.godspeed010.weblib.feature_library.domain.use_case

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import com.github.godspeed010.weblib.feature_library.domain.model.Folder
import com.github.godspeed010.weblib.feature_library.domain.repository.LibraryRepository
import com.github.godspeed010.weblib.feature_library.presentation.folders.FoldersState
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteFolderUseCase @Inject constructor(
    private val libraryRepo: LibraryRepository
) {
    private var deleteFolderJob: Job? = null

    operator fun invoke(
        scope: CoroutineScope,
        folder: Folder,
        getState: () -> FoldersState,
        updateState: (FoldersState) -> Unit
    ) {
        if (deleteFolderJob?.isActive == true) {
            deletePreviousFolder(getState, scope)
        }

        updateState(
            getState().copy(
                hiddenFolderId = folder.id,
                expandedDropdownItemListIndex = null
            )
        )

        deleteFolderJob = scope.launch(Dispatchers.Main) {
            val snackbarResult = getState().snackbarHostState.showSnackbar(
                message = "Deleted Folder",
                actionLabel = "Undo",
                duration = SnackbarDuration.Short
            )

            handleSnackbarResult(snackbarResult, folder, getState, updateState)
        }
    }

    private fun deletePreviousFolder(
        getState: () -> FoldersState,
        scope: CoroutineScope
    ) {
        Timber.d("DeleteFolder: deleteFolderJob is active; cancelling..")
        deleteFolderJob?.cancel()
        val lastDeletedFolderId = getState().hiddenFolderId
        scope.launch(Dispatchers.IO) {
            lastDeletedFolderId?.let {
                libraryRepo.deleteFolder(Folder(id = it, title = ""))
                Timber.d("DeleteFolder: Deleted previous Folder with id: $it")
            }
        }
    }

    private suspend fun handleSnackbarResult(
        snackbarResult: SnackbarResult,
        folderToDelete: Folder,
        getState: () -> FoldersState,
        updateState: (FoldersState) -> Unit,
    ) {
        when (snackbarResult) {
            SnackbarResult.Dismissed -> {
                withContext(Dispatchers.IO) {
                    libraryRepo.deleteFolder(folderToDelete)
                    Timber.d("DeleteFolder: Due to no Undo, deleted Folder with id: ${folderToDelete.id}")
                    updateState(getState().copy(hiddenFolderId = null))
                }
            }
            SnackbarResult.ActionPerformed -> updateState(getState().copy(hiddenFolderId = null))
        }
    }
}