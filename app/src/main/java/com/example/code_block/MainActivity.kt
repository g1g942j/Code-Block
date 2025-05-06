package com.example.code_block

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.code_block.ui.MainScreen
import com.example.code_block.ui.theme.CodeBlockTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CodeBlockTheme {
                MainScreen()
            }
        }
    }
}