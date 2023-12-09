package main

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

fun main() {
    val input = readInput(
        "Day06"
//        "Day06_test"
    )

    // part 1: multiple races
    val times = input[0].toRaceNumberList()
    val recordDistances = input[1].toRaceNumberList()
    val part1 = times.zip(recordDistances)
        .asSequence()
        .map { it.toRace().getWinCount() }
        .reduce(Int::times)

    part1.println()

    // part 2: one race
    val race = Race(input[0].toSingleRaceNumber(), input[1].toSingleRaceNumber())
    val part2 = race.getWinCount()
    part2.println()
}

fun Race.getWinCount(): Int {
    val sqrtDelta = sqrt((time * time - 4.0 * recordDistance))
    val x1 = floor(0.5 * (time - sqrtDelta) + 1).toInt()
    val x2 = ceil(0.5 * (time + sqrtDelta) - 1).toInt()

    return x2 - x1 + 1
}

data class Race(val time: Long, val recordDistance: Long)

fun Pair<Long, Long>.toRace() = Race(first, second)

fun String.toRaceNumberList(): List<Long> = substringAfter(":")
    .trim()
    .split(Regex("\\s+"))
    .map { it.toLong() }

fun String.toSingleRaceNumber(): Long = substringAfter(":")
    .trim()
    .replace(Regex("\\s+"), "")
    .toLong()
