package com.example.code_block.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var showMenu by remember { mutableStateOf(false) }
    var blocks by remember { mutableStateOf<List<String>>(emptyList()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = { showMenu = !showMenu },
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                    ) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
            )
        }
    ){ padding ->
        Box(modifier = Modifier.padding(padding)) {
            DropArea(
                blocks = blocks,
                onClear = { blocks = emptyList() },
                onRun = {}
            )

            AnimatedVisibility(
                visible = showMenu,
                enter = slideInHorizontally(),
                exit = slideOutHorizontally()
            ) {
                SideMenu(
                    onBlockAdded = { newBlock ->
                        blocks = blocks + newBlock
                    },
                    modifier = Modifier.width(250.dp).fillMaxHeight()
                )
            }
        }
    }
}