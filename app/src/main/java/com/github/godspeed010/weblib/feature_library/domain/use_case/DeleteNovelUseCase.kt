package com.github.godspeed010.weblib.feature_library.domain.use_case

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import com.github.godspeed010.weblib.feature_library.domain.model.Novel
import com.github.godspeed010.weblib.feature_library.domain.repository.LibraryRepository
import com.github.godspeed010.weblib.feature_library.presentation.novels.NovelsState
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalMaterial3Api::class)
@Singleton
class DeleteNovelUseCase @Inject constructor(
    private val libraryRepo: LibraryRepository
) {
    private var deleteNovelJob: Job? = null

    operator fun invoke(
        scope: CoroutineScope,
        novel: Novel,
        getState: () -> NovelsState,
        updateState: (NovelsState) -> Unit
    ) {
        if (deleteNovelJob?.isActive == true) {
            deletePreviousNovel(getState, scope)
        }

        updateState(
            getState().copy(
                hiddenNovelId = novel.id,
                expandedDropdownNovelListIndex = null,
            )
        )

        deleteNovelJob = scope.launch(Dispatchers.Main) {
            val snackbarResult = getState().snackbarHostState.showSnackbar(
                message = "Deleted Novel",
                actionLabel = "Undo",
                duration = SnackbarDuration.Short
            )

            handleSnackbarResult(snackbarResult, novel, getState, updateState)
        }
    }

    private fun deletePreviousNovel(
        getState: () -> NovelsState,
        scope: CoroutineScope
    ) {
        Timber.d("DeleteNovel: deleteNovelJob is active; cancelling..")
        deleteNovelJob?.cancel()
        val lastDeletedNovelId = getState().hiddenNovelId
        scope.launch(Dispatchers.IO) {
            lastDeletedNovelId?.let {
                libraryRepo.deleteNovel(Novel(id = it, title = "", url = "", folderId = 0, scrollProgression = 0f))
                Timber.d("DeleteNovel: Deleted previous Novel with id: $it")
            }
        }
    }

    private suspend fun handleSnackbarResult(
        snackbarResult: SnackbarResult,
        novelToDelete: Novel,
        getState: () -> NovelsState,
        updateState: (NovelsState) -> Unit,
    ) {
        when (snackbarResult) {
            SnackbarResult.Dismissed -> {
                withContext(Dispatchers.IO) {
                    libraryRepo.deleteNovel(novelToDelete)
                    Timber.d("DeleteNovel: Due to no Undo, deleted Novel with id: ${novelToDelete.id}")
                    updateState(getState().copy(hiddenNovelId = null))
                }
            }
            SnackbarResult.ActionPerformed -> updateState(getState().copy(hiddenNovelId = null))
        }
    }
}