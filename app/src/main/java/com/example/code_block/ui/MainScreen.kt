package com.example.code_block.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.code_block.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var showMenu by remember { mutableStateOf(false) }
    var blocks by remember { mutableStateOf(emptyList<String>()) }
    var showOutput by remember { mutableStateOf(false) }
    var outputText by remember { mutableStateOf("") }
    var editMode by remember { mutableStateOf(false) }

    val nestingLevels = remember(blocks) { calculateNestingLevels(blocks) }
    val errorIndices = remember(blocks) { validateCodeStructure(blocks) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                navigationIcon = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = stringResource(R.string.menu)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { editMode = !editMode },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = if (editMode) Icons.Default.Done else Icons.Default.Edit,
                            contentDescription = if (editMode) "Exit Edit" else "Edit Blocks"
                        )
                    }

                    Button(
                        onClick = {
                            if (errorIndices.isEmpty()) {
                                outputText = interpretCode(blocks)
                                showOutput = true
                            } else {
                                outputText = "Исправьте ошибки перед выполнением:\n" +
                                        errorIndices.joinToString { "#${it + 1}" }
                                showOutput = true
                            }
                        },
                        enabled = blocks.isNotEmpty()
                    ) {
                        Text(stringResource(R.string.run))
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
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
                onClear = { blocks = emptyList() },
                errorIndices = errorIndices,
                editMode = editMode
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