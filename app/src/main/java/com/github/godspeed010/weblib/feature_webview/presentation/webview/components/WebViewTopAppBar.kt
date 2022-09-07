package com.github.godspeed010.weblib.feature_webview.presentation.webview.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun WebViewTopAppBar(
    url: String,
    onUrlEntered: (String) -> Unit,
    onBackButtonClicked: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackButtonClicked) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
            }
        },
        title = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.White),
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    value = url,
                    onValueChange = onUrlEntered,
                    singleLine = true
                )
            }
        },
        actions = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Default.DarkMode, contentDescription = null)
            }
        }
    )
}

@Preview
@Composable
fun PreviewWebViewTopAppBar() {
    WebViewTopAppBar(
        url = "Hello there",
        onUrlEntered = {},
        onBackButtonClicked = {}
    )
}