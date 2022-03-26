package com.github.godspeed010.weblib.feature_library.presentation.novels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.godspeed010.weblib.feature_library.domain.model.Folder
import com.github.godspeed010.weblib.feature_library.domain.model.Novel
import com.github.godspeed010.weblib.feature_library.domain.use_case.novel.NovelUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

private val TAG = "NovelsViewModel"

@HiltViewModel
class NovelsViewModel @Inject constructor(
    private val novelUseCases: NovelUseCases,
    state: SavedStateHandle
) : ViewModel() {
    val folder: Folder = state.get<Folder>("folder")!!

    private val _novelsScreenState = mutableStateOf(NovelsState())
    val novelsScreenState: State<NovelsState> = _novelsScreenState

    fun onEvent(event: NovelsEvent) {
        when (event) {
            is NovelsEvent.AddNovel -> {
                Log.i(TAG, "Add Novel")

                //Add Novel
                viewModelScope.launch(Dispatchers.IO) {
                    novelUseCases.addNovel(
                        Novel(
                            folderId = folder.id,
                            title = novelsScreenState.value.dialogNovelTitle,
                            url = novelsScreenState.value.dialogNovelUrl
                        )
                    )

                    //Clear TextField states
                    _novelsScreenState.value = novelsScreenState.value.copy(
                        dialogNovelTitle = "",
                        dialogNovelUrl = "",
                    )
                }
                //Make AddEditNovelDialog Gone
                _novelsScreenState.value = novelsScreenState.value.copy(
                    isAddEditNovelDialogVisible = false
                )
            }
            is NovelsEvent.AddNovelClicked -> {
                Log.i(TAG, "AddNovelClicked")

                //make AddEditNovelDialog visible
                _novelsScreenState.value = novelsScreenState.value.copy(
                    isAddEditNovelDialogVisible = true
                )
            }
            is NovelsEvent.CancelNovelDialog -> {
                //Make Dialog Gone
                _novelsScreenState.value = novelsScreenState.value.copy(
                    isAddEditNovelDialogVisible = false,
                    dialogNovelTitle = "",
                    dialogNovelUrl = ""
                )
            }
            is NovelsEvent.DeleteNovel -> TODO()
            is NovelsEvent.EditNovelClicked -> TODO()
            is NovelsEvent.NovelClicked -> TODO()
            is NovelsEvent.RestoreNovel -> TODO()
            is NovelsEvent.UpdateNovel -> TODO()
            is NovelsEvent.EnteredNovelTitle -> {
                Log.i(TAG, "EnteredNovelTitle")

                //update the novelName State
                _novelsScreenState.value = novelsScreenState.value.copy(
                    dialogNovelTitle = event.novelTitle
                )
            }
            is NovelsEvent.EnteredNovelUrl -> {
                Log.i(TAG, "EnteredNovelUrl")

                //update the novelName State
                _novelsScreenState.value = novelsScreenState.value.copy(
                    dialogNovelUrl = event.novelUrl
                )
            }
        }
    }

    //todo should instead be getting novels using a Folder
    init {
        viewModelScope.launch {
            _novelsScreenState.value = novelsScreenState.value.copy(
                folderWithNovels = novelUseCases.getFolderWithNovels(folder.id)
            )
        }
    }
}