package main

import kotlin.math.abs

fun main() {
    val input = readInput(
        "Day11"
//        "Day11_test"
    )

    // part 1: twice as big
    val part1 = input.toExpandedUniverse(1)
        .galaxies
        .getSumOfPaths()
    part1.println()

    // part 2: one million times larger
    val part2 = input.toExpandedUniverse(999999)
        .galaxies
        .getSumOfPaths()
    part2.println()
}

fun Set<Galaxy>.getSumOfPaths(): Long {
    val notVisited = this.toMutableSet()
    return sumOf { galaxy ->
        notVisited.remove(galaxy)
        notVisited.sumOf { galaxy.getDistanceTo(it) }
    }
}

fun Galaxy.getDistanceTo(other: Galaxy): Long =
    abs(x - other.x) + abs(y - other.y)

fun List<String>.toExpandedUniverse(expansionFactor: Long): Universe {
    val expandedRows = getExpandedIndices()
    val expandedCols = transpose().getExpandedIndices()
    return Universe( toGalaxies(expansionFactor, expandedRows, expandedCols) )
}

fun List<String>.toGalaxies(
    expansionFactor: Long,
    expandedRows: Set<Int>,
    expandedCols: Set<Int>
) = flatMapIndexed { y, row ->
    row.mapIndexedNotNull { x, col ->
        if (col == '#') {
            val adjustedY = expansionFactor * expandedRows.count { it < y } + y
            val adjustedX = expansionFactor * expandedCols.count { it < x } + x
            Galaxy(adjustedX, adjustedY)
        } else null
    }
}.toSet()

fun List<String>.getExpandedIndices() = asSequence()
    .withIndex()
    .filter { (_, col) -> !col.contains('#') }
    .map { (x, _) -> x }
    .toSet()


fun List<String>.transpose(): List<String> =
    this[0].indices.map { y ->
        indices.map { x ->
            this[x][y]
        }.joinToString("")
    }


data class Universe(val galaxies: Set<Galaxy>)

data class Galaxy(val x: Long, val y: Long)