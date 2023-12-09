package main

import kotlin.math.pow

fun main() {
    val input = readInput(
        "Day09"
//        "Day09_test"
    )

    val report = input.toReport()

    val part1 = report.histories.sumOf { it.values.extrapolate() }
    part1.println()

    val part2 = report.histories.sumOf { it.values.reversed().extrapolate() }
//    val part2 = report.histories.sumOf { it.values.extrapolateBackwards() }
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
    return windowed(2) { (a, b) -> b - a}
}

data class Report(val histories: List<History>)

fun List<String>.toReport() = Report( map { it.toHistory() })

data class History(val values: List<Long>)

fun String.toHistory() = History(
    split(Regex("\\s+")).map { it.toLong() }
)