package com.github.godspeed010.weblib.feature_library.domain.model

import android.os.Parcelable

abstract class Item(
    open val createdAt: Long,
    open val lastModified: Long,
) : Parcelable