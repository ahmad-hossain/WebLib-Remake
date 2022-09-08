package com.github.godspeed010.weblib.feature_webview.presentation.webview.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.godspeed010.weblib.R

@Composable
fun WebViewTopAppBar(
    modifier: Modifier = Modifier,
    url: String,
    onUrlEntered: (String) -> Unit,
    onUrlSubmitted: () -> Unit,
    onBackButtonClicked: () -> Unit,
    onRefreshClicked: () -> Unit,
    onMoreOptionsClicked: () -> Unit
) {
    val ctx = LocalContext.current
    TopAppBar(
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onBackButtonClicked) {
                Icon(Icons.Default.ArrowBack, contentDescription = ctx.getString(R.string.cd_go_back))
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    value = url,
                    onValueChange = onUrlEntered,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { onUrlSubmitted() }
                    ),
                )
            }
        },
        actions = {
            IconButton(onClick = onRefreshClicked) {
                Icon(Icons.Default.Refresh, contentDescription = ctx.getString(R.string.cd_refresh_page))
            }
            IconButton(onClick = onMoreOptionsClicked) {
                Icon(Icons.Default.MoreVert, contentDescription = ctx.getString(R.string.cd_more_options))
            }
        }
    )
}

@Preview
@Composable
fun PreviewWebViewTopAppBar() {
    WebViewTopAppBar(
        Modifier, url = "Hello there", {}, {}, {}, {}, {}
    )
}