package com.example.code_block.ui

data class LoopState(
    val varName: String,
    var current: Int,
    val end: Int,
    val step: Int,
    val startblockCounter: Int,
    val nestingLevel: Int
)

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

fun validateCodeStructure(blocks: List<String>): List<Int> {
    val errorIndices = mutableListOf<Int>()
    var braceBalance = 0

    blocks.forEachIndexed { index, block ->
        val trimmed = block.trim()
        when {
            trimmed.endsWith("{") -> braceBalance++
            trimmed == "}" -> {
                braceBalance--
                if (braceBalance < 0) errorIndices.add(index)
            }

            trimmed.startsWith("}") -> errorIndices.add(index)
        }
    }

    if (braceBalance > 0) {
        blocks.forEachIndexed { index, block ->
            if (block.trim().endsWith("{")) errorIndices.add(index)
        }
    }

    return errorIndices.distinct()
}

fun interpretCode(blocks: List<String>): String {
    val output = StringBuilder()
    val errors = mutableListOf<String>()
    val variables = mutableMapOf<String, Any>()
    val declaredVariables = mutableSetOf<String>()

    val structureErrors = validateCodeStructure(blocks)
    if (structureErrors.isNotEmpty()) {
        errors.add("Структурные ошибки в блоках: ${structureErrors.joinToString { (it + 1).toString() }}")
    }

    fun precedence(op: Char): Int = when (op) {
        '+', '-' -> 1
        '*', '/', '%' -> 2
        else -> 0
    }

    fun toRPN(expression: String): List<String> {
        val output = mutableListOf<String>()
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
                    output.add(num.toString())
                }

                expression[i].isLetter() -> {
                    val varName = StringBuilder()
                    while (i < expression.length && (expression[i].isLetterOrDigit() || expression[i] == '_' || expression[i] == '.')) {
                        varName.append(expression[i++])
                    }
                    output.add(varName.toString())
                }

                expression[i] == '(' -> {
                    operators.add(expression[i++])
                }

                expression[i] == ')' -> {
                    while (operators.isNotEmpty() && operators.last() != '(') {
                        output.add(operators.last().toString())
                        operators.removeAt(operators.lastIndex)
                    }
                    operators.removeLastOrNull()
                    i++
                }

                else -> {
                    while (operators.isNotEmpty() && precedence(operators.last()) >= precedence(
                            expression[i]
                        )
                    ) {
                        output.add(operators.last().toString())
                        operators.removeAt(operators.lastIndex)
                    }
                    operators.add(expression[i++])
                }
            }
        }

        while (operators.isNotEmpty()) {
            output.add(operators.last().toString())
            operators.removeAt(operators.lastIndex)
        }

        return output
    }

    fun evaluateRPN(rpn: List<String>): Int {
        val stack = mutableListOf<Int>()

        for (token in rpn) {
            when {
                token.toIntOrNull() != null -> stack.add(token.toInt())
                token.endsWith(".length") -> {
                    val arrayName = token.removeSuffix(".length")
                    val array = variables[arrayName] as? IntArray
                    stack.add(array?.size ?: 0)
                }

                variables.containsKey(token) -> stack.add(variables[token] as? Int ?: 0)
                token in "+-*/%" -> {
                    if (stack.size < 2) return 0
                    val b = stack.removeAt(stack.lastIndex)
                    val a = stack.removeAt(stack.lastIndex)
                    stack.add(
                        when (token) {
                            "+" -> a + b
                            "-" -> a - b
                            "*" -> a * b
                            "/" -> if (b != 0) a / b else 0
                            "%" -> if (b != 0) a % b else 0
                            else -> 0
                        }
                    )
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

    fun String.containsAny(vararg strings: String): Boolean {
        return strings.any { this.contains(it) }
    }

    output.append("---ХОД РАБОТЫ---\n")
    blocks.forEach { block ->
        val trimmed = block.trim()
        when {
            trimmed.startsWith("int[]") && trimmed.contains("new array[") -> {
                val name = trimmed.substringBefore("=").trim().substringAfter("int[]").trim()
                declaredVariables.add(name)
                try {
                    val sizePart = trimmed.substringAfter("new array[").substringBefore("]").trim()
                    val size = evaluateRPN(toRPN(sizePart))
                    if (size < 0) {
                        variables[name] = IntArray(0)
                        output.append("Ошибка: размер массива не может быть отрицательным\n")
                    } else {
                        variables[name] = IntArray(size) { 0 }
                        output.append("Объявлен массив $name размером $size, инициализированный нулями\n")
                    }
                } catch (e: Exception) {
                    variables[name] = IntArray(0)
                    output.append("Ошибка создания массива $name: ${e.message}\n")
                }
            }

            trimmed.contains("[") && trimmed.contains("] =") -> {
                try {
                    val leftPart = trimmed.substringBefore("=").trim()
                    val rightPart = trimmed.substringAfter("=").substringBefore(";").trim()
                    val arrName = leftPart.substringBefore("[").trim()
                    val indexExpr = leftPart.substringAfter("[").substringBefore("]").trim()

                    val array = variables[arrName] as? IntArray
                    if (array == null) {
                        output.append("Ошибка: массив $arrName не найден\n")
                    } else {
                        val index = evaluateRPN(toRPN(indexExpr))

                        if (index !in array.indices) {
                            output.append("Ошибка: индекс $index выходит за границы массива $arrName (размер ${array.size})\n")
                        } else {
                            val value = evaluateRPN(toRPN(rightPart))

                            array[index] = value
                            output.append("$arrName[$index] = $value\n")
                        }
                    }
                } catch (e: Exception) {
                    output.append("Ошибка при присваивании элемента массива: ${e.message}\n")
                }
            }

            trimmed.contains("=") && trimmed.containsAny(
                "+",
                "-",
                "*",
                "/",
                "%"
            ) && trimmed.contains("[") -> {
                try {
                    val leftVar = trimmed.substringBefore("=").trim()
                    val rightExpr = trimmed.substringAfter("=").substringBefore(";").trim()

                    val operator = rightExpr.firstOrNull { it in "+-*/%" }
                        ?: throw Exception("Оператор не найден")

                    val parts = rightExpr.split(operator).map { it.trim() }
                    if (parts.size == 2) {
                        val leftPart = parts[0]
                        val arrayPart = parts[1]

                        val leftValue = when {
                            leftPart.toIntOrNull() != null -> leftPart.toInt()
                            variables.containsKey(leftPart) -> variables[leftPart] as? Int ?: 0
                            else -> throw Exception("Неизвестная переменная: $leftPart")
                        }

                        if (arrayPart.contains("[")) {
                            val arrName = arrayPart.substringBefore("[").trim()
                            val indexExpr =
                                arrayPart.substringAfter("[").substringBefore("]").trim()

                            val index = evaluateRPN(toRPN(indexExpr))
                            val array = variables[arrName] as? IntArray

                            if (array != null && index in array.indices) {
                                val arrayValue = array[index]
                                val result = when (operator) {
                                    '+' -> leftValue + arrayValue
                                    '-' -> leftValue - arrayValue
                                    '*' -> leftValue * arrayValue
                                    '/' -> if (arrayValue != 0) leftValue / arrayValue else 0
                                    '%' -> if (arrayValue != 0) leftValue % arrayValue else 0
                                    else -> throw Exception("Неподдерживаемый оператор: $operator")
                                }

                                variables[leftVar] = result
                                output.append("$leftVar = $result\n")
                            } else {
                                output.append("Ошибка: неверный индекс или массив\n")
                            }
                        }
                    }
                } catch (e: Exception) {
                    output.append("Ошибка при вычислении выражения: ${e.message}\n")
                }
            }

            trimmed.startsWith("int") && !trimmed.contains("=") -> {
                val names = trimmed.substringAfter("int")
                    .substringBefore(";")
                    .split(",")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                names.forEach { name ->
                    variables[name] = 0
                    declaredVariables.add(name)
                    output.append("Переменная $name = 0\n")
                }
            }

            trimmed.contains("=") && !trimmed.startsWith("int") && !trimmed.startsWith("if") && !trimmed.startsWith(
                "for"
            ) -> {
                val left = trimmed.substringBefore("=").trim()
                val right = trimmed.substringAfter("=").substringBefore(";").trim()
                if (left.contains("[")) {
                    val arrName = left.substringBefore("[").trim()
                    val indexExpr = left.substringAfter("[").substringBefore("]").trim()
                    val index = evaluateRPN(toRPN(indexExpr))
                    val value = evaluateRPN(toRPN(right))
                    (variables[arrName] as? IntArray)?.set(index, value)
                } else {
                    val value = evaluateRPN(toRPN(right))
                    variables[left] = value
                    if (left !in declaredVariables) declaredVariables.add(left)
                    output.append("Переменная $left = $value\n")
                }
            }
        }
    }

    val loopStack = mutableListOf<LoopState>()
    val conditionStack = mutableListOf<Boolean>()
    var currentNesting = 0
    var blockCounter = 0
    var skipUntilNesting: Int? = null

    while (blockCounter < blocks.size) {
        val block = blocks[blockCounter]
        val trimmed = block.trim()

        try {
            when {
                trimmed.startsWith("for") -> {
                    if (skipUntilNesting == null) {
                        val parts = trimmed.substringAfter("for (").substringBefore(")").split(";")
                        if (parts.size == 3) {
                            val init = parts[0].trim()
                            if (init.startsWith("int ")) {
                                val name = init.substringAfter("int ").substringBefore("=").trim()
                                val expr = init.substringAfter("=").trim()
                                val value = evaluateRPN(toRPN(expr))
                                variables[name] = value
                                declaredVariables.add(name)
                            }

                            val condition = parts[1].trim()
                            val conditionParts = condition.split("<=", "<", ">=", ">", "!=", "==")
                            if (conditionParts.size == 2) {
                                val varName = conditionParts[0].trim()
                                val endExpr = conditionParts[1].trim()
                                val end = evaluateRPN(toRPN(endExpr))

                                var step = 1
                                val stepPart = parts[2].trim()
                                if (stepPart.contains("+=")) {
                                    step = stepPart.substringAfter("+=").trim().toIntOrNull() ?: 1
                                } else if (stepPart.contains("-=")) {
                                    step = -1 * (stepPart.substringAfter("-=").trim().toIntOrNull()
                                        ?: 1)
                                } else if (stepPart.contains("++")) {
                                    step = 1
                                } else if (stepPart.contains("--")) {
                                    step = -1
                                }

                                val currentValue = variables[varName] as? Int ?: 0
                                loopStack.add(
                                    LoopState(
                                        varName,
                                        currentValue,
                                        end,
                                        step,
                                        blockCounter,
                                        currentNesting + 1
                                    )
                                )
                            }
                        }
                    }
                    currentNesting++
                    blockCounter++
                }

                trimmed == "}" -> {
                    if (loopStack.isNotEmpty() && loopStack.last().nestingLevel == currentNesting) {
                        val loop = loopStack.last()
                        loop.current += loop.step
                        variables[loop.varName] = loop.current

                        val shouldContinue = when {
                            loop.step > 0 -> loop.current < loop.end
                            loop.step < 0 -> loop.current > loop.end
                            else -> false
                        }

                        if (shouldContinue) {
                            blockCounter = loop.startblockCounter + 1
                            continue
                        } else {
                            loopStack.removeAt(loopStack.lastIndex)
                        }
                    }

                    if (skipUntilNesting != null && currentNesting <= skipUntilNesting!!) {
                        skipUntilNesting = null
                    }
                    currentNesting = maxOf(0, currentNesting - 1)
                    blockCounter++
                }

                trimmed.startsWith("if") -> {
                    val condition = trimmed.substringAfter("if").substringBefore("{").trim()
                    val conditionMet = evaluateCondition(condition.removeSurrounding("(", ")"))
                    conditionStack.add(conditionMet)

                    if (!conditionMet) {
                        skipUntilNesting = currentNesting
                    }
                    currentNesting++
                    blockCounter++
                }

                trimmed.startsWith("else") -> {
                    if (conditionStack.isNotEmpty()) {
                        val lastCondition = conditionStack.removeAt(conditionStack.size - 1)
                        if (lastCondition) {
                            skipUntilNesting = currentNesting
                        } else {
                            skipUntilNesting = null
                        }
                    }
                    currentNesting++
                    blockCounter++
                }

                else -> {
                    if (skipUntilNesting == null) {
                        when {
                            trimmed.startsWith("int[]") && trimmed.contains("new array[") -> {
                                val name =
                                    trimmed.substringBefore("=").trim().substringAfter("int[]")
                                        .trim()
                                declaredVariables.add(name)
                                try {
                                    val sizePart =
                                        trimmed.substringAfter("new array[").substringBefore("]")
                                            .trim()
                                    val size = evaluateRPN(toRPN(sizePart))
                                    if (size < 0) {
                                        variables[name] = IntArray(0)
                                        output.append("Ошибка: размер массива не может быть отрицательным\n")
                                    } else {
                                        variables[name] = IntArray(size) { 0 }
                                        output.append("Объявлен массив $name размером $size, инициализированный нулями\n")
                                    }
                                } catch (e: Exception) {
                                    variables[name] = IntArray(0)
                                    output.append("Ошибка создания массива $name: ${e.message}\n")
                                }
                            }

                            trimmed.contains("[") && trimmed.contains("] =") -> {
                                try {
                                    val leftPart = trimmed.substringBefore("=").trim()
                                    val rightPart =
                                        trimmed.substringAfter("=").substringBefore(";").trim()

                                    val arrName = leftPart.substringBefore("[").trim()
                                    val indexExpr =
                                        leftPart.substringAfter("[").substringBefore("]").trim()

                                    val array = variables[arrName] as? IntArray
                                    if (array == null) {
                                        output.append("Ошибка: массив $arrName не найден\n")
                                    } else {
                                        val index = evaluateRPN(toRPN(indexExpr))

                                        if (index !in array.indices) {
                                            output.append("Ошибка: индекс $index выходит за границы массива $arrName (размер ${array.size})\n")
                                        } else {
                                            val value = evaluateRPN(toRPN(rightPart))

                                            array[index] = value
                                            output.append("$arrName[$index] = $value\n")
                                        }
                                    }
                                } catch (e: Exception) {
                                    output.append("Ошибка при присваивании элемента массива: ${e.message}\n")
                                }
                            }

                            trimmed.contains("=") && trimmed.containsAny(
                                "+",
                                "-",
                                "*",
                                "/",
                                "%"
                            ) && trimmed.contains("[") -> {
                                try {
                                    val leftVar = trimmed.substringBefore("=").trim()
                                    val rightExpr =
                                        trimmed.substringAfter("=").substringBefore(";").trim()

                                    val operator = rightExpr.firstOrNull { it in "+-*/%" }
                                        ?: throw Exception("Оператор не найден")

                                    val parts = rightExpr.split(operator).map { it.trim() }
                                    if (parts.size == 2) {
                                        val leftPart = parts[0]
                                        val arrayPart = parts[1]

                                        val leftValue = when {
                                            leftPart.toIntOrNull() != null -> leftPart.toInt()
                                            variables.containsKey(leftPart) -> variables[leftPart] as? Int
                                                ?: 0

                                            else -> throw Exception("Неизвестная переменная: $leftPart")
                                        }

                                        if (arrayPart.contains("[")) {
                                            val arrName = arrayPart.substringBefore("[").trim()
                                            val indexExpr =
                                                arrayPart.substringAfter("[").substringBefore("]")
                                                    .trim()

                                            val index = evaluateRPN(toRPN(indexExpr))
                                            val array = variables[arrName] as? IntArray

                                            if (array != null && index in array.indices) {
                                                val arrayValue = array[index]
                                                val result = when (operator) {
                                                    '+' -> leftValue + arrayValue
                                                    '-' -> leftValue - arrayValue
                                                    '*' -> leftValue * arrayValue
                                                    '/' -> if (arrayValue != 0) leftValue / arrayValue else 0
                                                    '%' -> if (arrayValue != 0) leftValue % arrayValue else 0
                                                    else -> throw Exception("Неподдерживаемый оператор: $operator")
                                                }

                                                variables[leftVar] = result
                                                output.append("$leftVar = $result\n")
                                            } else {
                                                output.append("Ошибка: неверный индекс или массив\n")
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    output.append("Ошибка при вычислении выражения: ${e.message}\n")
                                }
                            }

                            trimmed.startsWith("int") && !trimmed.contains("=") -> {
                                val names = trimmed.substringAfter("int")
                                    .substringBefore(";")
                                    .split(",")
                                    .map { it.trim() }
                                    .filter { it.isNotEmpty() }
                                names.forEach { name ->
                                    variables[name] = 0
                                    declaredVariables.add(name)
                                }
                            }

                            trimmed.contains("=") && !trimmed.startsWith("int") -> {
                                val left = trimmed.substringBefore("=").trim()
                                val right = trimmed.substringAfter("=").substringBefore(";").trim()
                                if (left.contains("[")) {
                                    val arrName = left.substringBefore("[").trim()
                                    val indexExpr =
                                        left.substringAfter("[").substringBefore("]").trim()
                                    val index = evaluateRPN(toRPN(indexExpr))
                                    val value = evaluateRPN(toRPN(right))
                                    (variables[arrName] as? IntArray)?.set(index, value)
                                } else {
                                    val value = evaluateRPN(toRPN(right))
                                    variables[left] = value
                                    if (left !in declaredVariables) declaredVariables.add(left)
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

    if (errors.isNotEmpty()) {
        output.insert(0, "--- ОШИБКИ ---\n${errors.joinToString("\n")}\n\n")
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