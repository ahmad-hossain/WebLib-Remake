package com.github.godspeed010.weblib

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.github.godspeed010.weblib.feature_library.presentation.folders.FoldersScreen
import com.github.godspeed010.weblib.ui.theme.WebLibTheme
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WebLibTheme {
                App()
            }
        }
    }
}

@Composable
fun App() {
    FoldersScreen()
}