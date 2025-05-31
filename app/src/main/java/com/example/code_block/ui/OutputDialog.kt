package com.example.code_block.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import com.example.code_block.R

@Composable
fun OutputDialog(output: String, onDismiss: () -> Unit) {
    val isError = output.startsWith(stringResource(R.string.errors_header)) ||
            output.contains(stringResource(R.string.fix_errors_before_execution))

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (isError) stringResource(R.string.errors_found) else stringResource(R.string.execution_result))
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = output,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isError) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurface,
                    textAlign = if (isError) TextAlign.Start else TextAlign.Left
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isError) MaterialTheme.colorScheme.errorContainer
                    else MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(stringResource(R.string.close_message))
            }
        }
    )
}