package main

import kotlin.math.abs

fun main() {
    val input = readInput(
        "Day11"
//        "Day11_test"
    )

    val universe = input.toUniverse()

    // part 1: twice as big
    val part1 = universe
        .toExpandedGalaxies(1L)
        .calculateTotalDistance()
    part1.println()

    // part 2: one million times larger
    val part2 = universe
        .toExpandedGalaxies(999999)
        .calculateTotalDistance()
    part2.println()
}

fun Set<Galaxy>.calculateTotalDistance(): Long {
    val notVisited = this.toMutableSet()
    return sumOf { galaxy ->
        notVisited.remove(galaxy)
        notVisited.sumOf { galaxy.calculateDistanceTo(it) }
    }
}

fun Galaxy.calculateDistanceTo(other: Galaxy): Long =
    abs(x - other.x) + abs(y - other.y)

fun Universe.toExpandedGalaxies(expansionFactor: Long) = galaxies.map { galaxy ->
    val adjustedY = expansionFactor * expandedRows.count { it < galaxy.y } + galaxy.y
    val adjustedX = expansionFactor * expandedCols.count { it < galaxy.x } + galaxy.x
    Galaxy(adjustedX, adjustedY)
}.toSet()

fun List<String>.toUniverse(): Universe {
    val galaxies = toGalaxies()

    val xValues = galaxies.groupBy { it.x }.keys
    val yValues = galaxies.groupBy { it.y }.keys
    val expandedCols = this[0].indices.filterNot { it.toLong() in xValues }.toSet()
    val expandedRows = indices.filterNot { it.toLong() in yValues }.toSet()

    return Universe(galaxies, expandedRows, expandedCols)
}

fun List<String>.toGalaxies() = flatMapIndexed { y, row ->
    row.mapIndexedNotNull { x, col ->
        if (col == '#') {
            Galaxy(x.toLong(), y.toLong())
        } else null
    }
}.toSet()

data class Universe(val galaxies: Set<Galaxy>, val expandedRows: Set<Int>, val expandedCols: Set<Int>)

data class Galaxy(val x: Long, val y: Long)