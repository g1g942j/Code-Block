package com.example.code_block.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.code_block.R

@Composable
fun DropArea(
    blocks: List<String>,
    nestingLevels: List<Int>,
    onBlockChanged: (Int, String) -> Unit,
    onBlockRemoved: (Int) -> Unit,
    onClear: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(bottom = 16.dp)
        ) {
            itemsIndexed(blocks) { index, block ->
                BlockItem(
                    text = block,
                    index = index,
                    nestingLevel = nestingLevels[index],
                    onBlockChanged = onBlockChanged,
                    onBlockRemoved = onBlockRemoved
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onClear) {
                Text(stringResource(R.string.clear))
            }

        }
    }
}