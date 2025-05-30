package com.example.code_block.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier


import androidx.compose.ui.unit.dp



@Composable
fun DropArea(
    blocks: List<String>,
    nestingLevels: List<Int>,
    onBlockChanged: (Int, String) -> Unit,
    onBlockRemoved: (Int) -> Unit,
    onBlockMoved: (Int, Int) -> Unit,
    onClear: () -> Unit,
    editMode: Boolean
) {
    val listState = rememberLazyListState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            itemsIndexed(blocks) { index, block ->
                BlockItem(
                    text = block,
                    index = index,
                    nestingLevel = nestingLevels[index],
                    canMoveUp = index > 0,
                    canMoveDown = index < blocks.size - 1,
                    onBlockChanged = onBlockChanged,
                    onBlockRemoved = onBlockRemoved,
                    editMode = editMode,
                    onMoveUp = {
                        if (index > 0) onBlockMoved(index, index - 1)
                    },
                    onMoveDown = {
                        if (index < blocks.size - 1) onBlockMoved(index, index + 1)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Button(
            onClick = onClear,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Clear All")
        }
    }
}