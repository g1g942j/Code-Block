package com.example.code_block.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowDown

import androidx.compose.material.icons.filled.*
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme

@Composable
fun BlockItem(
    text: String,
    index: Int,
    nestingLevel: Int,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onBlockChanged: (Int, String) -> Unit,
    onBlockRemoved: (Int) -> Unit,
    editMode: Boolean,
    onMoveUp: (Int) -> Unit,
    onMoveDown: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedText by remember { mutableStateOf(text) }

    LaunchedEffect(text) {
        editedText = text
    }

    val blockColor = when {
        text.trim().endsWith("}") -> MaterialTheme.colorScheme.secondaryContainer
        text.trim().endsWith("{") -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surface
    }

    Box(modifier = modifier.padding(start = (nestingLevel * 24).dp)) {
        Card(
            colors = CardDefaults.cardColors(containerColor = blockColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isEditing || editMode) {
                if (isEditing) {
                    Column {
                        OutlinedTextField(
                            value = editedText,
                            onValueChange = { editedText = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(
                                onClick = { onMoveUp(index) },
                                enabled = canMoveUp,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowUp,
                                    contentDescription = "Move Up",
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            IconButton(
                                onClick = { onMoveDown(index) },
                                enabled = canMoveDown,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Move Down",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            IconButton(onClick = { onBlockRemoved(index) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }
                            IconButton(onClick = {
                                isEditing = false
                                onBlockChanged(index, editedText)
                            }) {
                                Icon(Icons.Default.Done, contentDescription = "Save")
                            }
                        }
                    }
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { isEditing = true }
                ) {
                    Text(
                        text = text,
                        modifier = Modifier.weight(1f).padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    IconButton(onClick = { isEditing = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                }
            }
        }
    }
}