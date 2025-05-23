package com.example.code_block.ui

data class LoopState(val varName: String,
                     var current: Int,
                     val end: Int,
                     val step: Int,
                     val startblockCounter: Int,
                     val nestingLevel: Int)

data class ConditionState(val conditionMet: Boolean, val nestingLevel: Int)
fun calculateNestingLevels(blocks: List<String>): List<Int> {
    var level = 0
    return blocks.map { block ->
        val trimmed = block.trim()
        when {
            trimmed.endsWith("{") -> {
                val currentLevel = level
                level++
                currentLevel
            }
            trimmed == "}" -> {
                level = maxOf(0, level - 1)
                level
            }
            else -> level
        }
    }
}

fun interpretCode(blocks: List<String>): String {
    val output = StringBuilder()
    val variables = mutableMapOf<String, Any>()
    val declaredVariables = mutableSetOf<String>()

    fun precedence(op: Char): Int = when (op) {
        '+', '-' -> 1
        '*', '/', '%' -> 2
        else -> 0
    }


    fun toRPN(expression: String): List<String> {
        val outPut = mutableListOf<String>()
        val operators = mutableListOf<Char>()
        var i = 0

        while (i < expression.length) {
            when {
                expression[i].isWhitespace() -> i++
                expression[i].isDigit() -> {
                    val num = StringBuilder()
                    while (i < expression.length && expression[i].isDigit()) {
                        num.append(expression[i++])
                    }
                    outPut.add(num.toString())
                }
                expression[i].isLetter() -> {
                    val varName = StringBuilder()
                    while (i < expression.length && (expression[i].isLetterOrDigit() || expression[i] == '_')) {
                        varName.append(expression[i++])
                    }
                    outPut.add(varName.toString())
                }
                expression[i] == '(' -> {
                    operators.add(expression[i++])
                }
                expression[i] == ')' -> {
                    while (operators.isNotEmpty() && operators.last() != '(') {
                        outPut.add(operators.last().toString())
                        operators.removeAt(operators.lastIndex)
                    }
                    if (operators.isNotEmpty()) {
                        operators.removeAt(operators.lastIndex)
                    }
                    i++
                }
                else -> {
                    while (operators.isNotEmpty() && precedence(operators.last()) >= precedence(expression[i])) {
                        outPut.add(operators.last().toString())
                        operators.removeAt(operators.lastIndex)
                    }
                    operators.add(expression[i++])
                }
            }
        }

        while (operators.isNotEmpty()) {
            outPut.add(operators.last().toString())
            operators.removeAt(operators.lastIndex)
        }

        return outPut
    }

    fun evaluateRPN(rpn: List<String>): Int {
        val stack = mutableListOf<Int>()

        for (token in rpn) {
            when {
                token.toIntOrNull() != null -> stack.add(token.toInt())
                variables.containsKey(token) -> stack.add(variables[token] as? Int ?: 0)
                token in "+-*/%" -> {
                    val b = stack.last()
                    stack.removeAt(stack.lastIndex)
                    val a = stack.last()
                    stack.removeAt(stack.lastIndex)
                    stack.add(when (token) {
                        "+" -> a + b
                        "-" -> a - b
                        "*" -> a * b
                        "/" -> if (b != 0) a / b else 0
                        "%" -> if (b != 0) a % b else 0
                        else -> 0
                    })
                }
                else -> stack.add(0)
            }
        }
        return stack.lastOrNull() ?: 0
    }
    fun evaluateCondition(condition: String): Boolean {
        val ops = listOf("==", "!=", ">=", "<=", ">", "<")
        val op = ops.firstOrNull { condition.contains(it) } ?: return false

        val parts = condition.split(op)
        if (parts.size != 2) return false

        val left = evaluateRPN(toRPN(parts[0].trim()))
        val right = evaluateRPN(toRPN(parts[1].trim()))

        return when (op) {
            "==" -> left == right
            "!=" -> left != right
            ">=" -> left >= right
            "<=" -> left <= right
            ">" -> left > right
            "<" -> left < right
            else -> false
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
                    val expr = trimmed.substringAfter("=").substringBefore(";").trim()
                    val value = evaluateRPN(toRPN(expr))
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
    var currentNesting = 0
    var blockCounter = 0

    while (blockCounter < blocks.size) {
        val block = blocks[blockCounter]
        val trimmed = block.trim()
        try {
            val shouldExecute = conditionStack.isEmpty() ||
                    conditionStack.any { it.conditionMet && it.nestingLevel >= currentNesting }

            when {
                trimmed.startsWith("for") -> {
                    val parts = trimmed.substringAfter("for (").substringBefore(")").split(";")
                    if (parts.size == 3) {
                        val init = parts[0].trim()
                        val condition = parts[1].trim()

                        if (init.startsWith("int ")) {
                            val name = init.substringAfter("int ").substringBefore("=").trim()
                            val expr = init.substringAfter("=").trim()
                            val value = evaluateRPN(toRPN(expr))
                            variables[name] = value
                        }
                        val conditionParts = condition.split("<=", "<", ">=", ">")
                        if (conditionParts.size == 2) {
                            val varName = conditionParts[0].trim()
                            val endExpr = conditionParts[1].trim()
                            val end = evaluateRPN(toRPN(endExpr))
                            loopStack.add(LoopState(varName, variables[varName] as Int, end, 1, blockCounter, currentNesting + 1))
                        }
                    }
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
                        conditionStack.removeAt(conditionStack.lastIndex)
                    }
                    currentNesting = maxOf(0, currentNesting - 1)
                    blockCounter++
                }

                trimmed.startsWith("if") -> {
                    val condition = trimmed.substringAfter("if").substringBefore("{").trim()
                    val conditionMet = evaluateCondition(condition.removeSurrounding("()"))
                    conditionStack.add(ConditionState(conditionMet, currentNesting + 1))
                    currentNesting++
                    blockCounter++
                }
                else -> {
                    if (shouldExecute) {
                        when {
                            trimmed.startsWith("int temp") -> {
                                val expr = trimmed.substringAfter("=").substringBefore(";").trim()
                                val value = evaluateRPN(toRPN(expr))
                                variables["temp"] = value
                            }

                            trimmed.contains("=") -> {
                                val left = trimmed.substringBefore("=").trim()
                                val right = trimmed.substringAfter("=").substringBefore(";").trim()

                                if (left.contains("[")) {
                                    val arrName = left.substringBefore("[").trim()
                                    val indexExpr = left.substringAfter("[").substringBefore("]").trim()
                                    val index = evaluateRPN(toRPN(indexExpr))
                                    val value = evaluateRPN(toRPN(right))
                                    (variables[arrName] as? IntArray)?.set(index, value)
                                } else {
                                    variables[left] = evaluateRPN(toRPN(right))
                                }
                            }
                        }
                    }
                    blockCounter++
                }
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