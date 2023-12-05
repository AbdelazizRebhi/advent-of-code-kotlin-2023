package main

import kotlin.system.measureTimeMillis

fun main() {

    val input = readInput(
        "Day05"
//        "Day05_test"
    )

    val headerIndices = Category.entries.map { category ->
        input.indexOfFirst { category.name.lowercase() in it }
    }


    val mappings = buildList {
        Category.entries.sortedBy { it.ordinal }.forEach { category ->
            val i = category.ordinal
            val start = headerIndices[i] + 1
            val end = if (i < 6) headerIndices[i + 1] - 1 else input.size
            val mapLines = input.subList(start, end).map(String::toMapLine)
            add(Mapping(category, mapLines))
        }
    }

    // part 1: first line is seed numbers
    val seeds = input[0].substringAfter(": ")
        .split(Regex("\\s+")).map { it.toLong() }

    val part1 = seeds.minOf { seed ->
        var location = seed
        mappings.forEach { mapping ->
            mapping.mapLines.firstOrNull { location in it.sourceRange }
                ?.let { location += it.targetRange.first - it.sourceRange.first }
        }
        return@minOf location
    }

    part1.println()

    // part 2: first line is seed ranges
    val seedRanges = input[0].substringAfter(": ")
        .split(Regex("\\s+")).map { it.toLong() }
        .chunked(2) { (start, length) -> start until start + length }

//
//    val part2ForwardTime = measureTimeMillis {
//        part2Forward(seedRanges, mappings).println()
//    }
//    println("Part 2 with the forward approach took $part2ForwardTime ms.") // around 7 min


    val part2BackwardTime = measureTimeMillis {
        part2Backward(seedRanges, mappings).println()
    }
    println("Part 2 with the backward approach took $part2BackwardTime ms.") // under 400 ms

}

private fun part2Backward(seedRanges: List<LongRange>, mappings: List<Mapping>): Long {
    var minLocation: Long = Long.MAX_VALUE
    for (location in 1..Long.MAX_VALUE) {
        var seed = location
        mappings.reversed().forEach { mapping ->
            mapping.mapLines.firstOrNull { seed in it.targetRange }
                ?.let { seed += it.sourceRange.first - it.targetRange.first }
        }

        if (seedRanges.any { it.contains(seed) }) {
            minLocation = location
            break
        }
    }
    return minLocation
}

private fun part2Forward(seedRanges: List<LongRange>, mappings: List<Mapping>): Long {
    var minLocation: Long = Long.MAX_VALUE

    seedRanges.forEach { seedRange ->
        seedRange.forEach { seed ->
            var location = seed
            mappings.forEach { mapping ->
                mapping.mapLines.firstOrNull { location in it.sourceRange }
                    ?.let { location += it.targetRange.first - it.sourceRange.first }
            }
            if (location < minLocation) {
                minLocation = location
            }
        }
    }

    return minLocation
}

enum class Category { SOIL, FERTILIZER, WATER, LIGHT, TEMPERATURE, HUMIDITY, LOCATION }

data class Mapping(val targetCategory: Category, val mapLines: List<MapLine>)


data class MapLine(val targetRange: LongRange, val sourceRange: LongRange)

fun String.toMapLine(): MapLine {
    val (targetStart, sourceStart, rangeLength) = trim().split(Regex("\\s+")).map { it.toLong() }
    return MapLine(
        targetStart until targetStart  + rangeLength,
        sourceStart until sourceStart  + rangeLength
    )
}

