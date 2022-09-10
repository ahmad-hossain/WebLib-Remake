package com.github.godspeed010.weblib.feature_library.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Folder(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String
) : Item()
