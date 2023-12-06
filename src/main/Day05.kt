package main

import kotlin.system.measureTimeMillis

fun main() {

    val input = readInput(
        "Day05"
//        "Day05_test"
    )

    val converters = input.toConverterList()

    // part 1: first line is seed numbers
    val seeds = input[0].substringAfter(": ")
        .split(Regex("\\s+")).map { it.toLong() }

    val part1 = seeds.minOf(converters::convertSeedToLocation)

    part1.println()

    // part 2: first line is seed ranges
    val seedRanges = input[0].substringAfter(": ")
        .split(Regex("\\s+")).map { it.toLong() }
        .chunked(2) { (start, length) -> start until start + length }

    val part2Time = measureTimeMillis {
        part2(seedRanges, converters.reversed()).println()
    }
    println("Part 2 with the backward approach took $part2Time ms.")

}

fun part2(seedRanges: List<LongRange>, converters: List<Converter>): Long {
    var minLocation: Long = Long.MAX_VALUE
    for (location in 1..Long.MAX_VALUE) {
        val seed = converters.convertLocationToSeed(location)

        if (seedRanges.any { it.contains(seed) }) {
            minLocation = location
            break
        }
    }
    return minLocation
}

enum class Category { SOIL, FERTILIZER, WATER, LIGHT, TEMPERATURE, HUMIDITY, LOCATION }

data class Converter(val targetCategory: Category, val mapLines: List<MapLine>)

fun List<String>.toConverterList(): List<Converter> {
    val headerIndices = Category.entries.map { category ->
        indexOfFirst { category.name.lowercase() in it }
    }

    return headerIndices.mapIndexed { index, headerIndex ->
        val start = headerIndex + 1
        val end = if (index < 6) headerIndices[index + 1] - 1 else size
        val mapLines = subList(start, end).map(String::toMapLine)
        Converter(Category.entries[index], mapLines)
    }
}

fun Converter.convertValue(value: Long): Long =
    mapLines.firstOrNull { value in it.sourceRange }
        ?.let { value + it.targetRange.first - it.sourceRange.first }
        ?: value

fun Converter.inverseConvertValue(value: Long): Long =
    mapLines.firstOrNull { value in it.targetRange }
        ?.let { value + it.sourceRange.first - it.targetRange.first }
        ?: value

fun List<Converter>.convertSeedToLocation(seed: Long): Long =
    fold(seed) { acc, converter ->
        converter.convertValue(acc)
    }

fun List<Converter>.convertLocationToSeed(location: Long): Long =
    fold(location) { acc, converter ->
        converter.inverseConvertValue(acc)
    }

data class MapLine(val targetRange: LongRange, val sourceRange: LongRange)

fun String.toMapLine(): MapLine {
    val (targetStart, sourceStart, rangeLength) = trim().split(Regex("\\s+")).map { it.toLong() }
    return MapLine(
        targetStart until targetStart  + rangeLength,
        sourceStart until sourceStart  + rangeLength
    )
}

