package com.github.godspeed010.weblib.test_util

import androidx.annotation.StringRes
import androidx.test.platform.app.InstrumentationRegistry

internal fun getString(@StringRes id: Int) =
    InstrumentationRegistry.getInstrumentation().targetContext.resources.getString(id)
