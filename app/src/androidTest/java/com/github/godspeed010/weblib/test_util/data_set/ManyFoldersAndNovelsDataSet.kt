package com.github.godspeed010.weblib.test_util.data_set

import com.github.godspeed010.weblib.feature_library.domain.model.Folder
import com.github.godspeed010.weblib.feature_library.domain.model.Novel

object ManyFoldersAndNovelsDataSet {
    val FOLDERS = listOf(
        Folder(id = 1, title = "folder1"),
        Folder(id = 2, title = "folder2"),
        Folder(id = 3, title = "folder3"),
        Folder(id = 4, title = "folder4"),
    )
    val NOVELS = listOf(
        Novel(
            id = 0,
            title = "novel1",
            url = "https://google.com",
            scrollProgression = 0f,
            folderId = 1
        ),
        Novel(
            id = 1,
            title = "novel2",
            url = "https://example.com",
            scrollProgression = 0f,
            folderId = 1
        ),
        Novel(
            id = 2,
            title = "novel3",
            url = "https://bing.com",
            scrollProgression = 0f,
            folderId = 3
        ),
    )
}
