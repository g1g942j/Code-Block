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

    Column(
        modifier = modifier
            .width(200.dp)
            .padding(8.dp)
    ) {
        Text(
            text = context.getString(R.string.variable),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        listOf(
            R.string.variable_declaration_text to R.string.variable_declaration,
            R.string.variable_assignment_text to R.string.variable_assignment,
        ).forEach { (textRes, labelRes) ->
            Button(
                onClick = {
                    onBlockAdded(context.getString(textRes))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(context.getString(labelRes))
            }
        }

        Text(
            text = context.getString(R.string.array),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Button(
            onClick = {
                onBlockAdded(context.getString(R.string.array_text))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text(context.getString(R.string.array))
        }

        Text(
            text = "Control Structures",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        listOf(
            R.string.for_loop_text to R.string.loop,
            R.string.if_text to R.string.condition,
            R.string.close_block to R.string.close
        ).forEach { (textRes, labelRes) ->
            Button(
                onClick = {
                    onBlockAdded(context.getString(textRes))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(context.getString(labelRes))
            }
        }

        Text(
            text = "Examples",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Button(
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
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