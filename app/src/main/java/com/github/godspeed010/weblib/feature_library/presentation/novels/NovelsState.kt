package com.github.godspeed010.weblib.feature_library.presentation.novels

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.TextFieldValue
import com.github.godspeed010.weblib.R
import com.github.godspeed010.weblib.feature_library.domain.model.Folder
import com.github.godspeed010.weblib.feature_library.domain.model.Novel

data class NovelsState(
    val novels: List<Novel> = emptyList(),
    val isAddEditNovelDialogVisible: Boolean = false,
    val dialogTitleRes: Int = R.string.dialog_add_novel,
    val dialogIcon: ImageVector = Icons.Outlined.BookmarkAdd,
    val dialogNovelTitle: TextFieldValue = TextFieldValue(""),
    val dialogNovelUrl: TextFieldValue = TextFieldValue(""),
    val expandedDropdownNovelListIndex: Int? = null,
    val snackbarHostState: SnackbarHostState = SnackbarHostState(),
    val hiddenNovelId: Int? = null,
    val isBottomSheetVisible: Boolean = false,
    val folders: List<Folder> = emptyList(),
)