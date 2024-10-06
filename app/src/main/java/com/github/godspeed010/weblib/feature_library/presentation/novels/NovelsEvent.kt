package com.github.godspeed010.weblib.feature_library.presentation.novels

import androidx.compose.ui.text.input.TextFieldValue
import com.github.godspeed010.weblib.feature_library.domain.model.Folder
import com.github.godspeed010.weblib.feature_library.domain.model.Novel

sealed class NovelsEvent {
    data object NovelDialogConfirmed : NovelsEvent()
    data class MoveNovel(val novel: Novel) : NovelsEvent()
    data class DeleteNovel(val novel: Novel) : NovelsEvent()
    data object FabClicked : NovelsEvent()
    data class EditNovelClicked(val novel: Novel) : NovelsEvent()
    data object CancelNovelDialog : NovelsEvent()
    data class EnteredNovelTitle(val novelTitle: TextFieldValue) : NovelsEvent()
    data class EnteredNovelUrl(val novelUrl: TextFieldValue) : NovelsEvent()
    data class MoreOptionsClicked(val index: Int) : NovelsEvent()
    data object MoreOptionsDismissed : NovelsEvent()
    data object BottomSheetDismissed : NovelsEvent()
    data class BottomSheetFolderClicked(val folder: Folder) : NovelsEvent()
}