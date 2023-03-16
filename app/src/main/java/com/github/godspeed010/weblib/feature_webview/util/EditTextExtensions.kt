package com.github.godspeed010.weblib.feature_webview.util

import android.annotation.SuppressLint
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import timber.log.Timber

@SuppressLint("SoonBlockedPrivateApi", "DiscouragedPrivateApi")
fun EditText.setCursorDrawableColorFilter(colorFilter: PorterDuffColorFilter) {
    try {
        val fCursorDrawableRes = TextView::class.java.getDeclaredField("mCursorDrawableRes")
        fCursorDrawableRes.isAccessible = true
        val mCursorDrawableRes = fCursorDrawableRes.getInt(this)
        val fEditor = TextView::class.java.getDeclaredField("mEditor")
        fEditor.isAccessible = true
        val editor = fEditor[this]
        val clazz: Class<*> = editor.javaClass
        val fCursorDrawable = clazz.getDeclaredField("mCursorDrawable")
        fCursorDrawable.isAccessible = true
        val drawables = arrayOfNulls<Drawable>(2)

        drawables[0] = ContextCompat.getDrawable(context, mCursorDrawableRes)
        drawables[1] = ContextCompat.getDrawable(context, mCursorDrawableRes)
        drawables[0]?.colorFilter = colorFilter
        drawables[1]?.colorFilter = colorFilter
        fCursorDrawable[editor] = drawables
    } catch (e: Throwable) {
        Timber.e(e, "setCursorDrawableColor: Could not set cursor drawable color")
    }
}