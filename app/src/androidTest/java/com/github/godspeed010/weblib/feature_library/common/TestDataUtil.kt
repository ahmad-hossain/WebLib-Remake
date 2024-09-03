package com.github.godspeed010.weblib.feature_library.common

import com.github.godspeed010.weblib.feature_library.domain.model.Folder
import com.github.godspeed010.weblib.feature_library.domain.model.Novel

internal object TestDataUtil {
    val FOLDER_1 = Folder.createWithDefaults(id = 1, title = "testFolder")
    val FOLDER_2 = Folder.createWithDefaults(id = 2, title = "foo bar")

    fun createFolders(count: Int): List<Folder> {
        return (1..count).map(::createFolderWithId)
    }

    private fun createFolderWithId(id: Int): Folder {
        return Folder.createWithDefaults(
            id = id,
            title = "title$id"
        )
    }

    fun createNovels(count: Int, folderId: Int = 0): List<Novel> {
        return (1..count).map { createNovelWithId(it, folderId) }
    }

    fun createNovelWithId(id: Int, folderId: Int = 0): Novel {
        return Novel.createWithDefaults(
            id = id,
            title = "title$id",
            url = "url$id",
            folderId = folderId
        )
    }
}