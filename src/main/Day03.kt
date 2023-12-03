package main

fun main() {

    val input = readInput(
        "Day03"
//        "Day03_test"
    )

    val candidateParts = findCandidateParts(input)
    val symbolCells = findSymbols(input)

    val realParts = findRealParts(candidateParts, symbolCells)
    val gears = findGears(realParts, symbolCells)

    val part1 = realParts.sumOf { it.value.toInt() }
    val part2 = gears.sumOf { it.ratio }

    part1.println()
    part2.println()
}

val XY_RANGE = 1..139

data class Cell(val x: Int, val y: Int)

data class CellRange(val xRange: IntRange, val y: Int, val value: String) {
    private val adjacentXRange: IntRange
        get() = (xRange.first-1).coerceAtLeast(XY_RANGE.first()) .. (xRange.last+1).coerceAtMost(XY_RANGE.last)

    private val adjacentYRange: IntRange
        get() = (y-1).coerceAtLeast(XY_RANGE.first()) .. (y+1).coerceAtMost(XY_RANGE.last)

    val cells: Set<Cell>
        get() = xRange.map { x -> Cell(x,y) }.toSet()

    val neighbors: Set<Cell>
        get() = buildSet {
            adjacentXRange.forEach { x ->
                adjacentYRange.forEach {y ->
                    add(Cell(x, y))
                }
            }
        }
}

fun MatchResult.toCellRange(lineNumber: Int) = CellRange (
    range,
    lineNumber,
    value
)

fun List<String>.toCellRangeOf(pattern: String) = this.flatMapIndexed { lineNumber, line ->
    pattern.toRegex().findAll(line).map { it.toCellRange(lineNumber) }
}.toSet()

fun findCandidateParts(inputLines: List<String>) = inputLines.toCellRangeOf("\\d+")

fun findSymbols(inputLines: List<String>) = inputLines.toCellRangeOf("[^\\w.]")

fun findRealParts(candidateParts: Set<CellRange>, symbols: Set<CellRange>): Set<CellRange> {
    val symbolCells = symbols.flatMap { it.cells }.toSet()

    return candidateParts
        .filter { it.neighbors.intersect(symbolCells).isNotEmpty() }.toSet()
}

data class Gear(val partPair: Pair<Int, Int>) {
    val ratio: Int
        get() = partPair.first * partPair.second
}

fun findGears(realParts: Set<CellRange>, symbols: Set<CellRange>) = buildSet {
    val starCells = symbols.filter { it.value == "*" }.flatMap { it.cells }.toSet()

    starCells.forEach { starCell ->
        val neighborParts = realParts.filter { starCell in it.neighbors }
        if (neighborParts.size == 2) {
            add(Gear(neighborParts.first().value.toInt() to neighborParts.last().value.toInt()))
        }
    }
}