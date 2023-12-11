package main

import kotlin.math.abs

fun main() {
    val input = readInput(
        "Day11"
//        "Day11_test"
    )

    val galaxies = input.toExpandedUniverse().galaxies

    val part1 = galaxies.getSumOfPaths()
    part1.println()
}

fun Set<Galaxy>.getSumOfPaths(): Long {
    val notVisited = this.toMutableSet()
    return sumOf { galaxy ->
        notVisited.remove(galaxy)
        galaxy.getSumOfPaths(notVisited)
    }
}

fun Galaxy.getSumOfPaths(other: Set<Galaxy>) =
    other.sumOf { getDistanceTo(it) }

fun List<String>.toExpandedUniverse(): Universe {
    val expansionFactor = 999999L
    val expandedRows = this@toExpandedUniverse.getExpandedIndices()

    val transposed = transpose()
    val expandedCols = transposed.getExpandedIndices()

    return Universe( toGalaxies(expansionFactor, expandedRows, expandedCols) )
}

private fun List<String>.toGalaxies(
    expansionFactor: Long,
    expandedRows: Set<Int>,
    expandedCols: Set<Int>
) = buildSet {
    this@toGalaxies.forEachIndexed { y, row ->
        if (row.contains('#')) {
            val adjustedY = expansionFactor.times(expandedRows.count { it < y }).plus(y)
            row.forEachIndexed { x, col ->
                if (col == '#') {
                    val adjustedX = expansionFactor.times(expandedCols.count { it < x }).plus(x)
                    add(Galaxy(adjustedX, adjustedY))
                }
            }
        }
    }
}

private fun List<String>.getExpandedIndices() = buildSet {
    this@getExpandedIndices.forEachIndexed { x, col ->
        if (!col.contains('#')) {
            add(x)
        }
    }
}

fun List<String>.transpose(): List<String> {
    return this[0].indices.map { y -> indices.map { x -> this[x][y] }.joinToString("") }
}

data class Universe(val galaxies: Set<Galaxy>)

data class Galaxy(val x: Long, val y: Long)

fun Galaxy.getDistanceTo(other: Galaxy): Long =
    abs(x - other.x) + abs(y - other.y)