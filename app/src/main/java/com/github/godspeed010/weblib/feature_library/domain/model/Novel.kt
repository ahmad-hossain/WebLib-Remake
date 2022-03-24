package com.github.godspeed010.weblib.feature_library.domain.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Folder::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("folderId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.NO_ACTION
        )],
    indices = [
        Index(value = arrayOf("folderId")),
        Index(value = arrayOf("id"), unique = true)
    ]
)
data class Novel(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val title: String,
    val url: String,
    val folderId: Int
) : Parcelable