package main

fun main() {

    val input = readInput(
        "Day03"
//        "Day03_test"
    )

    val candidateParts = findNumbers(input)
    val symbols = findSymbols(input)

    val realParts = findRealParts(candidateParts, symbols)
    val gears = findGears(realParts, symbols)

    val part1 = realParts.sumOf { it.value }
    val part2 = gears.sumOf { it.ratio }

    part1.println()
    part2.println()
}

val XY_RANGE = 1..139

data class Cell(val x: Int, val y: Int)

data class CellRange(val xRange: IntRange, val y: Int) {
    private val adjacentXRange: IntRange
        get() = (xRange.first-1).coerceAtLeast(XY_RANGE.first()) .. (xRange.last+1).coerceAtMost(XY_RANGE.last)

    private val adjacentYRange: IntRange
        get() = (y-1).coerceAtLeast(XY_RANGE.first()) .. (y+1).coerceAtMost(XY_RANGE.last)

    val cells: Set<Cell>
        get() = xRange.map { x -> Cell(x,y) }.toSet()

    val neighbors: Set<Cell>
        get() = buildSet {
            adjacentXRange.forEach { x ->
                adjacentYRange.forEach { y ->
                    add(Cell(x, y))
                }
            }
        }
}

data class Number(val cellRange: CellRange, val value: Int)

data class Symbol(val cellRange: CellRange, val value: Char)

fun MatchResult.toCellRange(lineNumber: Int) = CellRange (
    range,
    lineNumber
)

fun MatchResult.toNumber(lineNumber: Int) = Number (
    toCellRange(lineNumber),
    value.toInt()
)

fun MatchResult.toSymbol(lineNumber: Int) = Symbol (
    toCellRange(lineNumber),
    value.first()
)

fun findNumbers(inputLines: List<String>) = inputLines.flatMapIndexed { lineNumber, line ->
    "\\d+".toRegex().findAll(line).map { it.toNumber(lineNumber) }
}.toSet()

fun findSymbols(inputLines: List<String>) = inputLines.flatMapIndexed { lineNumber, line ->
    "[^\\w.]".toRegex().findAll(line).map { it.toSymbol(lineNumber) }
}.toSet()

fun Set<Number>.filterByNeighborsOf(reference: Set<Cell>) =
    filter { it.cellRange.neighbors.intersect(reference).isNotEmpty() }.toSet()

fun findRealParts(candidateParts: Set<Number>, symbols: Set<Symbol>) = candidateParts
    .filterByNeighborsOf(symbols.flatMap { it.cellRange.cells }.toSet())

data class Gear(val partPair: Pair<Int, Int>) {
    val ratio: Int
        get() = partPair.first * partPair.second
}

fun findGears(realParts: Set<Number>, symbols: Set<Symbol>) = buildSet {
    symbols
        .filter { it.value == '*' }
        .flatMap { it.cellRange.cells }
        .forEach { starCell ->
            val neighborParts = realParts.filter { starCell in it.cellRange.neighbors }
            if (neighborParts.size == 2) {
                add(Gear(neighborParts.first().value to neighborParts.last().value))
            }
        }
}