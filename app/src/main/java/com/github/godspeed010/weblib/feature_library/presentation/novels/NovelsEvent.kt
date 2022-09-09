package com.github.godspeed010.weblib.feature_library.presentation.novels

import com.github.godspeed010.weblib.feature_library.domain.model.Novel

sealed class NovelsEvent {
    object AddOrUpdateNovel : NovelsEvent()
    data class MoveNovel(val novel: Novel) : NovelsEvent()
    data class DeleteNovel(val novel: Novel) : NovelsEvent()
    object NovelClicked : NovelsEvent()
    object RestoreNovel : NovelsEvent()
    object FabClicked : NovelsEvent()
    data class EditNovelClicked(val novel: Novel) : NovelsEvent()
    object CancelNovelDialog : NovelsEvent()
    data class EnteredNovelTitle(val novelTitle: String) : NovelsEvent()
    data class EnteredNovelUrl(val novelUrl: String) : NovelsEvent()
    data class MoreOptionsClicked(val novelId: Int) : NovelsEvent()
    object MoreOptionsDismissed : NovelsEvent()
}
