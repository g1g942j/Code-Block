package com.example.code_block.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.code_block.R

@Composable
fun BlockItem(
    text: String,
    index: Int,
    nestingLevel: Int,
    hasError: Boolean,
    onBlockChanged: (Int, String) -> Unit,
    onBlockRemoved: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedText by remember { mutableStateOf(text) }

    LaunchedEffect(text) {
        editedText = text
    }

    val blockColor = when {
        hasError -> MaterialTheme.colorScheme.errorContainer
        text.trim() == stringResource(R.string.close_block) ->
            MaterialTheme.colorScheme.secondaryContainer
        text.trim().endsWith("{") ->
            MaterialTheme.colorScheme.primaryContainer
        else ->
            MaterialTheme.colorScheme.tertiaryContainer
    }

    Box(
        modifier = modifier.padding(start = (nestingLevel * 24).dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = blockColor),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(min = 100.dp, max = 300.dp)
        ) {
            if (isEditing) {
                Column {
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
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = {
                                isEditing = false
                                onBlockChanged(index, editedText)
                            }
                        ) {
                            Icon(
                                Icons.Filled.Done,
                                contentDescription = stringResource(R.string.save)
                            )
                        }
                        IconButton(onClick = { onBlockRemoved(index) }) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = stringResource(R.string.delete)
                            )
                        }
                    }
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { isEditing = true }
                        .fillMaxWidth()
                ) {
                    Text(
                        text = text,
                        modifier = Modifier
                            .weight(1f)
                            .padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    IconButton(onClick = { isEditing = true }) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = stringResource(R.string.edit)
                        )
                    }
                }
            }
        }
    }
}