package com.example.code_block.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.code_block.R

@Composable
fun MenuButton(textRes: String, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Text(textRes)
    }
}

@Composable
fun SideMenu(onBlockAdded: (String) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant)) {
        Text(stringResource(R.string.block_message), style = MaterialTheme.typography.headlineSmall)

        val blockTypes = listOf(
            stringResource(R.string.variable) to stringResource(R.string.variable_text),
            stringResource(R.string.cycle) to stringResource(R.string.cycle_text),
            stringResource(R.string.condition) to stringResource(R.string.condition_text),
            stringResource(R.string.function) to stringResource(R.string.function_text)
        )

        blockTypes.forEach { (name, code) ->
            MenuButton(textRes = name, onClick = { onBlockAdded(code) })
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}