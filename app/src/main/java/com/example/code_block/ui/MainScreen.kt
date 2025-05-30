package com.example.code_block.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.code_block.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var showMenu by remember { mutableStateOf(false) }
    var blocks by remember { mutableStateOf(emptyList<String>()) }
    var showOutput by remember { mutableStateOf(false) }
    var outputText by remember { mutableStateOf("") }

    val nestingLevels = remember(blocks) { calculateNestingLevels(blocks) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                navigationIcon = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(
                            Icons.Default.Menu,
                            stringResource(R.string.menu)
                        )
                    }
                },
                actions = {
                    Button(onClick = {
                        outputText = interpretCode(blocks)
                        showOutput = true
                    }) {
                        Text(stringResource(R.string.run))
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues).fillMaxSize()
        ) {
            DropArea(
                blocks = blocks,
                nestingLevels = nestingLevels,
                onBlockChanged = { i, t ->
                    blocks = blocks.toMutableList().apply { set(i, t) }
                },
                onBlockRemoved = { i ->
                    blocks = blocks.toMutableList().apply { removeAt(i) }
                },
                onBlockMoved = { from, to ->
                    blocks = blocks.toMutableList().apply {
                        if (from < to) {
                            add(to, removeAt(from))
                        } else {
                            val item = removeAt(from)
                            add(to, item)
                        }
                    }
                },
                onClear = { blocks = emptyList() }
            )

            AnimatedVisibility(
                visible = showMenu,
                enter = slideInHorizontally { -it },
                exit = slideOutHorizontally { -it }
            ) {
                SideMenu(
                    onBlockAdded = { newBlock ->
                        blocks = blocks + newBlock
                        showMenu = false
                    },
                    modifier = Modifier.fillMaxHeight()
                )
            }
        }
    }

    if (showOutput) {
        OutputDialog(
            output = outputText,
            onDismiss = { showOutput = false }
        )
    }
}
