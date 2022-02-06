package com.github.godspeed010.weblib.feature_library.domain.model.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.github.godspeed010.weblib.feature_library.domain.model.Folder
import com.github.godspeed010.weblib.feature_library.domain.model.Novel

data class FolderWithNovel(
    @Embedded val folder: Folder,
    @Relation(
        parentColumn = "id",
        entityColumn = "folderId"
    )
    val novels: List<Novel>
)
