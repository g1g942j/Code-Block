package com.example.code_block.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.code_block.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DropArea(
    blocks: List<String>,
    nestingLevels: List<Int>,
    onBlockChanged: (Int, String) -> Unit,
    onBlockRemoved: (Int) -> Unit,
    onBlockMoved: (Int, Int) -> Unit,
    onClear: () -> Unit,
    errorIndices: List<Int>,
    editMode: Boolean
) {
    val listState = rememberLazyListState()
    var draggedItem by remember { mutableStateOf<Int?>(null) }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            itemsIndexed(blocks) { index, block ->
                Box(
                    modifier = Modifier
                        .pointerInput(Unit) {
                            detectDragGesturesAfterLongPress(
                                onDragStart = { draggedItem = index },
                                onDragEnd = { draggedItem = null },
                                onDragCancel = { draggedItem = null },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    val newIndex = listState
                                        .layoutInfo
                                        .visibleItemsInfo
                                        .firstOrNull { item ->
                                            item.index != index &&
                                                    change.position.y >= item.offset &&
                                                    change.position.y <= item.offset + item.size
                                        }?.index ?: index

                                    if (newIndex != index) {
                                        scope.launch {
                                            onBlockMoved(index, newIndex)
                                        }
                                    }
                                }
                            )
                        }
                ) {
                    BlockItem(
                        text = block,
                        index = index,
                        nestingLevel = nestingLevels[index],
                        hasError = errorIndices.contains(index),
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
                        modifier = Modifier
                            .alpha(if (draggedItem == index) 0.5f else 1f)
                            .fillMaxWidth()
                    )
                }
            }
        }

        Button(
            onClick = onClear,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.clear))
        }
    }
}