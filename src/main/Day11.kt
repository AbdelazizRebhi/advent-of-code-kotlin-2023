package main

fun main() {
    val input = readInput(
//        "Day11"
        "Day11_test"
    )

    input.toExpandedUniverse().println()



}

fun List<String>.toExpandedUniverse(): Universe {
    val expandedRows = mutableListOf<List<Char>>()
    forEach { line ->
        expandedRows.add(line.toList())
        if (!line.contains('#')) {
            expandedRows.add(line.toList())
        }
    }
//    expandedRows.transpose().forEach { it.println() }

    val expandedCols = mutableListOf<List<Char>>()
    expandedRows.transpose().forEach { line ->
        expandedCols.add(line.toList())
        if (!line.contains('#')) {
            expandedCols.add(line.toList())
        }
    }
    expandedCols.transpose().forEach { it.println() }

    val galaxies = expandedCols.transpose()
        .flatMapIndexed { y, line ->
            line.mapIndexed { x, c ->
                if (c == '#') Galaxy(Cell(x, y)) else null
            }.filterNotNull()
        }.toSet()
    return Universe(galaxies)
}

fun List<List<Char>>.transpose(): List<List<Char>> {
    return this[0].indices.map { y -> indices.map { x -> this[x][y] } }
}

data class Universe(val galaxies: Set<Galaxy>)

data class Galaxy(val cell: Cell)