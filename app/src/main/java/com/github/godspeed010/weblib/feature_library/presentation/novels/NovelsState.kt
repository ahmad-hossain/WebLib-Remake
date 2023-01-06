package com.github.godspeed010.weblib.feature_library.presentation.novels

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.godspeed010.weblib.R
import com.github.godspeed010.weblib.feature_library.domain.model.Novel
import com.github.godspeed010.weblib.feature_library.domain.model.relations.FolderWithNovel

data class NovelsState(
    val folderWithNovels: LiveData<FolderWithNovel> = MutableLiveData(),
    val isAddEditNovelDialogVisible: Boolean = false,
    val dialogTitleRes: Int = R.string.dialog_add_novel,
    val dialogIcon: ImageVector = Icons.Outlined.BookmarkAdd,
    val dialogNovel: Novel = Novel.createWithDefaults(),
    val expandedDropdownNovelListIndex: Int? = null,
)
