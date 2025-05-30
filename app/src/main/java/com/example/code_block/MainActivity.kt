package com.example.code_block

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.code_block.ui.MainScreen
import com.example.code_block.ui.theme.CodeBlockTheme
import com.example.code_block.ui.theme.SplashBackground

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        window.setBackgroundDrawable(ColorDrawable(SplashBackground.toArgb()))
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            CodeBlockTheme {
                MainScreen()
            }
        }
    }
}