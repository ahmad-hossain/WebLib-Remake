package com.github.godspeed010.weblib.feature_library.common.components

import android.R.attr.label
import android.R.attr.value
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.github.godspeed010.weblib.ui.theme.WebLibTheme

@PreviewTest
@Preview(showBackground = true)
@Composable
fun FixedWidthOutlinedTextField_emptyTextContent() {
    WebLibTheme {
        FixedWidthOutlinedTextField(
            value = "",
            label = { Text("Title") },
            onValueChange = {},
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        )
    }
}
