package com.example.code_block.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.example.code_block.R

@Composable
fun BlockItem(
    text: String,
    index: Int,
    nestingLevel: Int,
    hasError: Boolean,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onBlockChanged: (Int, String) -> Unit,
    onBlockRemoved: (Int) -> Unit,
    editMode: Boolean,
    onMoveUp: (Int) -> Unit,
    onMoveDown: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val isUneditable = text.trim().let { trimmed ->
        trimmed == stringResource(R.string.close_block) || trimmed.startsWith("else")
    }

    var isEditing by remember { mutableStateOf(false) }
    var editedText by remember { mutableStateOf(text) }

    LaunchedEffect(text) {
        editedText = text
    }

    val blockColor = when {
        hasError -> MaterialTheme.colorScheme.errorContainer
        text.trim() == stringResource(R.string.close_block) ->
            MaterialTheme.colorScheme.secondaryContainer
        text.trim().startsWith("else") ->
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
        text.trim().endsWith("{") ->
            MaterialTheme.colorScheme.primaryContainer
        text.trim().endsWith("}") ->
            MaterialTheme.colorScheme.secondaryContainer
        else ->
            MaterialTheme.colorScheme.surface
    }

    Box(modifier = modifier.padding(start = (nestingLevel * 24).dp)) {
        Card(
            colors = CardDefaults.cardColors(containerColor = blockColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isEditing || editMode) {
                Column {
                    if (isEditing) {
                        OutlinedTextField(
                            value = editedText,
                            onValueChange = { editedText = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    isEditing = false
                                    onBlockChanged(index, editedText)
                                }
                            )
                        )
                    }

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
                                contentDescription = stringResource(R.string.move_up),
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
                                contentDescription = stringResource(R.string.move_down),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        IconButton(onClick = { onBlockRemoved(index) }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = stringResource(R.string.delete)
                            )
                        }

                        if (isEditing) {
                            IconButton(onClick = {
                                isEditing = false
                                onBlockChanged(index, editedText)
                            }) {
                                Icon(
                                    Icons.Default.Done,
                                    contentDescription = stringResource(R.string.save)
                                )
                            }
                        }
                    }
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable(enabled = !isUneditable) { isEditing = true }
                        .fillMaxWidth()
                ) {
                    Text(
                        text = text,
                        modifier = Modifier
                            .weight(1f)
                            .padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (!isUneditable) {
                        IconButton(
                            onClick = { isEditing = true },
                            enabled = !isUneditable
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = stringResource(R.string.edit)
                            )
                        }
                    }
                }
            }
        }
    }
}