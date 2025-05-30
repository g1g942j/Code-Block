package com.example.code_block.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.code_block.R

@Composable
fun SideMenu(
    onBlockAdded: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val blockTemplates = listOf(
        R.string.variable_text to R.string.variable,
        R.string.array_text to R.string.array,
        R.string.for_loop_text to R.string.loop,
        R.string.if_text to R.string.condition,
        R.string.close_block to R.string.close
    )

    Column(
        modifier = modifier.width(240.dp).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            "Добавить блок",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        blockTemplates.forEach { (textRes, labelRes) ->
            FilledTonalButton(
                onClick = { onBlockAdded(context.getString(textRes)) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(context.getString(labelRes))
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 12.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )

        Text(
            "Примеры алгоритмов",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        FilledTonalButton(
            onClick = {
                listOf(
                    R.string.bubble_sort_array,
                    R.string.bubble_sort_loop1,
                    R.string.bubble_sort_loop2,
                    R.string.bubble_sort_if,
                    R.string.bubble_sort_temp,
                    R.string.bubble_sort_swap1,
                    R.string.bubble_sort_swap2,
                    R.string.bubble_sort_close,
                    R.string.bubble_sort_close,
                    R.string.bubble_sort_close
                ).forEach { resId ->
                    onBlockAdded(context.getString(resId))
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(context.getString(R.string.bubble_sort))
        }
        Button(
            onClick = {
                listOf(
                    R.string.if_checker_var_declaration,
                    R.string.if_checker_assignment,
                    R.string.if_checker_if,
                    R.string.if_checker_if_body,
                    R.string.if_checker_close
                ).forEach { resId ->
                    onBlockAdded(context.getString(resId))
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text("If Checker")
        }
        Button(
            onClick = {
                listOf(
                    R.string.for_checker_var_declaration,
                    R.string.for_checker_loop,
                    R.string.for_checker_loop_body,
                    R.string.for_checker_close
                ).forEach { resId ->
                    onBlockAdded(context.getString(resId))
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text("For Checker")
        }
    }
}