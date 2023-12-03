package main

fun main() {
    fun part1(input: List<String>): Int {
        val partCandidates = findPartCandidates(input)
        val symbolCells = findSymbolCells(input)

        return partCandidates
            .filter { it.neighbors.intersect(symbolCells).isNotEmpty() }
            .map(PartCandidate::value)
            .sum()
    }

    fun part2(input: List<String>): Int = input.sumOf { it.length }

    val input = readInput(
        "Day03"
//        "Day03_test"
    )

    part1(input).println()

    part2(input).println()
}

val XY_RANGE = 1..139

val numberRegex = "\\d+".toRegex()
val symbolRegex = "[^\\w.]".toRegex()

data class Cell(val x: Int, val y: Int)
data class PartCandidate(val xRange: IntRange, val y: Int, val value: Int) {
    private val adjacentXRange: IntRange
        get() = (xRange.first-1).coerceAtLeast(XY_RANGE.first()) .. (xRange.last+1).coerceAtMost(XY_RANGE.last)

    private val adjacentYRange: IntRange
        get() = (y-1).coerceAtLeast(XY_RANGE.first()) .. (y+1).coerceAtMost(XY_RANGE.last)


    val neighbors: Set<Cell>
        get() = buildSet {
            adjacentXRange.forEach { x ->
                adjacentYRange.forEach {y ->
                    add(Cell(x, y))
                }
            }
        }
}

fun MatchResult.toPartCandidate(lineNumber: Int) = PartCandidate (
    range,
    lineNumber,
    value.toInt()
)

fun findPartCandidates(inputLines: List<String>) = buildSet {
    inputLines.forEachIndexed { lineNumber, line ->
        addAll(numberRegex.findAll(line).map {matchResult -> matchResult.toPartCandidate(lineNumber) })
    }
}

fun MatchResult.toSymbolCell(lineNumber: Int) = Cell (
    range.first,
    lineNumber
)

fun findSymbolCells(inputLines: List<String>) = buildSet{
    inputLines.forEachIndexed { lineNumber, line ->
        addAll(symbolRegex.findAll(line).map { matchResult -> matchResult.toSymbolCell(lineNumber) })
    }
}