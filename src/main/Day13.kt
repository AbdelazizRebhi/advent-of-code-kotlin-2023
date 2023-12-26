package main

import kotlin.math.min

fun main() {
    val input = readEntireInput(
        "Day13"
//        "Day13_test"
    )

    val patterns = input.split("\r\n\r\n").map {
        it.split("\r\n").toPattern()
    }

    patterns.sumOf {
        it.summarizeReflections { other -> this == other }
    }.println()
    patterns.sumOf {
        it.summarizeReflections { other -> this.isAlmostEqual(other) }
    }.println()
}

fun Pattern.summarizeReflections(condition: List<Int>.(List<Int>) -> Boolean): Int {
    val c = cols.findReflection(condition)
    val r = rows.findReflection(condition)
    return c + r * 100
}

fun List<Int>.findReflection(condition: List<Int>.(List<Int>) -> Boolean): Int {
    (1 until size).forEach { i ->
        val s = min(i, size - i)
        if (subList(i - s, i).reversed().condition(subList(i, i + s))) {
            return i
        }
    }
    return 0
}

fun List<Int>.isAlmostEqual(other: List<Int>): Boolean {
    if (size != other.size) {
        return false
    }
    var smudgeFound = false
    indices.forEach { i ->
        val xorRes = this[i] xor other[i]
        val isSmudge = xorRes != 0 && xorRes and (xorRes - 1) == 0
        if (xorRes != 0 && (!isSmudge || smudgeFound)) {
            return false
        }
        if (isSmudge) {
            smudgeFound = true
        }
    }
    return smudgeFound
}

fun List<String>.toPattern(): Pattern {
    val height = size
    val width = first().length

    val powerOfTwoRow = IntArray(width) { 1 shl it }
    val powerOfTwoCol = IntArray(height) { 1 shl it }

    val rows = MutableList(height) { 0 }
    val cols = MutableList(width) { 0 }
    forEachIndexed { y, line ->
        line.forEachIndexed { x, char ->
            if (char == '#') {
                rows[y] += powerOfTwoRow[x]
                cols[x] += powerOfTwoCol[y]
            }
        }
    }
    return Pattern(rows, cols)
}
data class Pattern(val rows: List<Int>, val cols: List<Int>)

