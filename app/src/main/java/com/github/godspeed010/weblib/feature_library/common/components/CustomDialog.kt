package com.github.godspeed010.weblib.feature_library.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@ExperimentalComposeUiApi
@Composable
fun CustomDialog(
    modifier: Modifier = Modifier,
    onDismissDialog: () -> Unit,
    title: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit
) {
    Dialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismissDialog,
    ) {
        Card(
            modifier = modifier.width(IntrinsicSize.Min),
            shape = RoundedCornerShape(16.dp),
            backgroundColor = MaterialTheme.colors.surface,
            elevation = 24.dp
        ) {
            Column {
                title()
                Spacer(Modifier.height(16.dp))
                content()
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp, end = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    confirmButton()
                    Spacer(Modifier.width(8.dp))
                    dismissButton()
                }
            }
        }
    }
}