package com.example.code_block.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.code_block.R

@Composable
fun SideMenu(
    onBlockAdded: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val arrayInit = stringResource(R.string.array_init)
    val declareArray = stringResource(R.string.declare_array)
    val arrayAssignment = stringResource(R.string.array_assignment)

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
                    .padding(vertical = 3.dp)
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
                onBlockAdded(arrayInit)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp)
        ) {
            Text(declareArray)
        }

        Button(
            onClick = {
                onBlockAdded(arrayAssignment)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp)
        ) {
            Text(stringResource(R.string.array_assignment_text))
        }

        Text(
            text = context.getString(R.string.control_structures),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        listOf(
            R.string.for_loop_text to R.string.loop,
            R.string.if_text to R.string.condition,
            R.string.else_text to R.string.else_block,
            R.string.close_block to R.string.close
        ).forEach { (textRes, labelRes) ->
            Button(
                onClick = {
                    onBlockAdded(context.getString(textRes))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
            ) {
                Text(context.getString(labelRes))
            }
        }

        Text(
            text = context.getString(R.string.tests),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Button(
            onClick = {
                listOf(
                    R.string.bs_array_init,
                    R.string.bs_array_1,
                    R.string.bs_array_2,
                    R.string.bs_array_3,
                    R.string.bs_array_4,
                    R.string.bs_array_5,
                    R.string.bs_temp_var,
                    R.string.bs_outer_loop,
                    R.string.bs_inner_loop,
                    R.string.bs_condition,
                    R.string.bs_swap1,
                    R.string.bs_swap2,
                    R.string.bs_swap3,
                    R.string.close_block,
                    R.string.close_block,
                    R.string.close_block
                ).forEach { resId ->
                    onBlockAdded(context.getString(resId))
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp)
        ) {
            Text(context.getString(R.string.bubble_sort))
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
                .padding(vertical = 3.dp)
        ) {
            Text(stringResource(R.string.for_checker))
        }
        Button(
            onClick = {
                listOf(
                    R.string.if_else_var_declaration,
                    R.string.if_else_assignment,
                    R.string.if_else_if,
                    R.string.if_else_if_body,
                    R.string.if_else_close,
                    R.string.if_else_else,
                    R.string.if_else_else_body,
                    R.string.if_else_close
                ).forEach { resId ->
                    onBlockAdded(context.getString(resId))
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp)
        ) {
            Text(context.getString(R.string.if_else_checker))
        }
        Button(
            onClick = {
                listOf(
                    R.string.array_checker_init,
                    R.string.array_checker_loop1,
                    R.string.array_checker_assign,
                    R.string.close_block,
                    R.string.array_checker_sum_decl,
                    R.string.array_checker_loop2,
                    R.string.array_checker_sum,
                    R.string.close_block
                ).forEach { resId ->
                    onBlockAdded(context.getString(resId))
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp)
        ) {
            Text(stringResource(R.string.array_checker))
        }
    }
}