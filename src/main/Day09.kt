package main

import kotlin.math.pow

suspend fun main() {
    val input = readInput(
        "Day09"
//        "Day09_test"
    )

    val report = input.toReport()

    val part1 = report.histories.parallelMap { it.values.extrapolate() }.sum()
    part1.println()

    val part2 = report.histories.parallelMap { it.values.reversed().extrapolate() }.sum()
//    val part2 = report.histories.parallelMap { it.values.extrapolateBackwards() }.sum()
    part2.println()

}

tailrec fun List<Long>.extrapolate(acc: Long = 0): Long {
    val differences = getDifferences()
    return if (differences.all { it == 0L }) {
        acc + last()
    } else {
        differences.extrapolate(acc + last())
    }
}

tailrec fun List<Long>.extrapolateBackwards(acc: Long = 0, ops: Int = 0): Long {
    val differences = getDifferences()
    return if (differences.all { it == 0L }) {
        (first() - acc) * (-1.0).pow(ops).toLong()
    } else {
        differences.extrapolateBackwards(first() - acc, ops + 1)
    }
}

fun List<Long>.getDifferences(): List<Long> {
    return (1 until size).map { this[it] - this[it - 1] }
}

data class Report(val histories: List<History>)

suspend fun List<String>.toReport() = Report( parallelMap { it.toHistory() })

data class History(val values: List<Long>)

fun String.toHistory() = History(
    split(Regex("\\s+")).map { it.toLong() }
)