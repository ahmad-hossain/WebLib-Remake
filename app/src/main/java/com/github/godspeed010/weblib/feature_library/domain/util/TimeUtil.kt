package com.github.godspeed010.weblib.feature_library.domain.util

object TimeUtil {
    fun currentTimeSeconds(): Long = System.currentTimeMillis() / 1000
}