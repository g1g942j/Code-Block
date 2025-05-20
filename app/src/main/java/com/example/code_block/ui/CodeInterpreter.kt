package com.example.code_block.ui

data class LoopState(
    val varName: String,
    var current: Int,
    val end: Int,
    val step: Int,
    val startblockCounter: Int,
    val nestingLevel: Int
)

data class ConditionState(
    val conditionMet: Boolean,
    val nestingLevel: Int
)

fun calculateNestingLevels(blocks: List<String>): List<Int> {
    var level = 0
    return blocks.map {
        when {
            it.trim().endsWith("{") -> level++.let { level - 1 }
            it.trim() == "}" -> level--
            else -> level
        }
    }
}

fun interpretCode(blocks: List<String>): String {
    val output = StringBuilder()
    val variables = mutableMapOf<String, Any>()
    val declaredVariables = mutableSetOf<String>()
    var currentNesting = 0

    fun evaluateExpression(expr: String): Int {
        return try {
            when {
                expr.toIntOrNull() != null -> expr.toInt()
                expr == "temp" -> variables["temp"] as? Int ?: 0
                expr.contains("arr.length") -> {
                    (variables["arr"] as? IntArray)?.size ?: 0
                }
                expr.contains("[") -> {
                    val arrName = expr.substringBefore("[").trim()
                    val index = evaluateExpression(expr.substringAfter("[").substringBefore("]").trim())
                    (variables[arrName] as? IntArray)?.getOrElse(index) { 0 } ?: 0
                }
                expr.contains("+") -> {
                    expr.split("+").sumOf { evaluateExpression(it.trim()) }
                }
                else -> variables[expr] as? Int ?: 0
            }
        } catch (e: Exception) {
            0
        }
    }

    output.append("---НАЧАЛЬНЫЕ ЗНАЧЕНИЯ---\n")
    blocks.forEach { block ->
        val trimmed = block.trim()
        when {
            trimmed.startsWith("int[]") -> {
                val name = trimmed.substringAfter("int[]").substringBefore("=").trim()
                declaredVariables.add(name)
                try {
                    val values = trimmed.substringAfter("{")
                        .substringBefore("}")
                        .split(",")
                        .map { it.trim().toInt() }
                        .toIntArray()
                    variables[name] = values
                    output.append("Массив $name: [${values.joinToString()}]\n")
                } catch (e: Exception) {
                    variables[name] = intArrayOf()
                    output.append("Ошибка инициализации массива $name: ${e.message}\n")
                }
            }
            trimmed.startsWith("int") && trimmed.contains("=") -> {
                val name = trimmed.substringAfter("int").substringBefore("=").trim()
                declaredVariables.add(name)
                try {
                    val value = evaluateExpression(trimmed.substringAfter("=").substringBefore(";").trim())
                    variables[name] = value
                    output.append("Переменная $name = $value\n")
                } catch (e: Exception) {
                    variables[name] = 0
                    output.append("Ошибка инициализации переменной $name: ${e.message}\n")
                }
            }
        }
    }

    val loopStack = mutableListOf<LoopState>()
    val conditionStack = mutableListOf<ConditionState>()

    var blockCounter = 0
    while (blockCounter < blocks.size) {
        val block = blocks[blockCounter]
        val trimmed = block.trim()

        try {
            when {
                trimmed.startsWith("for") -> {
                    val varName = trimmed.substringAfter("for (int ").substringBefore("=").trim()
                    val start = evaluateExpression(trimmed.substringAfter("=").substringBefore(";").trim())
                    val endExpr = trimmed.substringAfter("<").substringBefore(";").trim()
                    val end = evaluateExpression(endExpr)
                    loopStack.add(LoopState(varName, start, end, 1, blockCounter, currentNesting + 1))
                    variables[varName] = start
                    currentNesting++
                    blockCounter++
                }
                trimmed == "}" -> {
                    if (loopStack.isNotEmpty() && loopStack.last().nestingLevel == currentNesting) {
                        val loop = loopStack.last()
                        loop.current++
                        variables[loop.varName] = loop.current
                        if (loop.current < loop.end) {
                            blockCounter = loop.startblockCounter + 1
                            continue
                        } else {
                            loopStack.removeAt(loopStack.lastIndex)
                        }
                    }
                    if (conditionStack.isNotEmpty() && conditionStack.last().nestingLevel == currentNesting) {
                        conditionStack.removeAt(loopStack.lastIndex)
                    }
                    currentNesting = maxOf(0, currentNesting - 1)
                    blockCounter++
                }
                trimmed.startsWith("if") -> {
                    val condition = trimmed.substringAfter("if").substringBefore("{").trim()
                    val conditionMet = evaluateExpression(condition.removeSurrounding("()")) > 0
                    conditionStack.add(ConditionState(conditionMet, currentNesting + 1))
                    currentNesting++
                    blockCounter++
                }
                trimmed.startsWith("int temp") -> {
                    val value = evaluateExpression(trimmed.substringAfter("=").substringBefore(";").trim())
                    variables["temp"] = value
                    blockCounter++
                }
                trimmed.contains("=") -> {
                    val left = trimmed.substringBefore("=").trim()
                    val right = trimmed.substringAfter("=").substringBefore(";").trim()

                    if (left.contains("[")) {
                        val arrName = left.substringBefore("[").trim()
                        val index = evaluateExpression(left.substringAfter("[").substringBefore("]").trim())
                        val value = evaluateExpression(right)
                        (variables[arrName] as? IntArray)?.set(index, value)
                    } else {
                        variables[left] = evaluateExpression(right)
                    }
                    blockCounter++
                }
                else -> blockCounter++
            }
        } catch (e: Exception) {
            output.append("\nОшибка в строке ${blockCounter + 1}: ${e.message}\n")
            blockCounter++
        }
    }

    output.append("\n---ИТОГОВЫЕ ЗНАЧЕНИЯ---\n")
    declaredVariables.sorted().forEach { name ->
        when (val value = variables[name]) {
            is IntArray -> output.append("Массив $name: [${value.joinToString()}]\n")
            is Int -> output.append("Переменная $name = $value\n")
            else -> output.append("$name: неинициализировано\n")
        }
    }

    return output.toString()
}