package com.github.godspeed010.weblib.feature_library.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Folder(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val title: String
)
