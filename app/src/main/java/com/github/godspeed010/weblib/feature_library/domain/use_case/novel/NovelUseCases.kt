package com.github.godspeed010.weblib.feature_library.domain.use_case.novel

data class NovelUseCases(
    val getNovels: GetNovels,
    val addNovel: AddNovel,
    val getFolderWithNovels: GetFolderWithNovels
)
