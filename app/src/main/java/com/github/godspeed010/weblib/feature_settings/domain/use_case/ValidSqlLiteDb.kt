package com.github.godspeed010.weblib.feature_settings.domain.use_case

import android.app.Application
import android.net.Uri
import android.provider.OpenableColumns
import timber.log.Timber
import javax.inject.Inject

class ValidSqlLiteDb @Inject constructor(
    private val app: Application,
) {

    fun isValid(uri: Uri?): Boolean {
        val hasValidFileExtension = uri?.fileName?.fileExtension() == "db"
        val isValidSqlLiteDb = uri?.hasValidSQLiteDb() ?: false
        Timber.d("isValid: hasValidFileExtension=$hasValidFileExtension, isValidSqlLiteDb=$isValidSqlLiteDb")

        return hasValidFileExtension && isValidSqlLiteDb
    }

    private val Uri.fileName: String?
        get() {
            val cursor = app.contentResolver.query(this, null, null, null, null) ?: return null
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            val res = cursor.getString(nameIndex)
            cursor.close()
            return res
        }

    private fun String.fileExtension(): String = this.substringAfterLast(".")

    private fun Uri.hasValidSQLiteDb(): Boolean {
        try {
            val inputStream = app.contentResolver.openInputStream(this) ?: return false
            inputStream.use {
                val buffer = ByteArray(16)
                it.read(buffer, 0, 16)
                val str = String(buffer)
                return str == "SQLite format 3\u0000"
            }
        } catch (e: Exception) {
            Timber.e(e, "isValidSQLiteDb")
            return false
        }
    }

}