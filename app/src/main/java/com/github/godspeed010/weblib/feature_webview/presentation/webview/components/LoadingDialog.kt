package com.github.godspeed010.weblib.feature_webview.presentation.webview.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun LoadingDialog(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    bodyText: String,
) {
    if (!isVisible) return
    Dialog(
        onDismissRequest = {},
    ) {
        Card(
            modifier = modifier,
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = AlertDialogDefaults.containerColor,
                contentColor = AlertDialogDefaults.titleContentColor
            ),
        ) {

            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator()
                Spacer(Modifier.height(8.dp))
                Text(bodyText)
            }
        }
    }
}

@Preview
@Composable
fun PreviewLoadingDialog() {
    LoadingDialog(isVisible = true, bodyText = "Loading Saved Scroll Progress")
}