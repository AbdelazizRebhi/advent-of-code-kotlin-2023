package main

fun main() {

    val input = readInput(
        "Day03"
//        "Day03_test"
    )

    val symbols = input.findSymbols()

    val realParts = input.findNumbers().findRealParts(symbols)
    val gears = symbols.findGears(realParts)

    val part1 = realParts.sumOf { it.value }
    val part2 = gears.sumOf { it.ratio }

    part1.println()
    part2.println()
}

val XY_RANGE = 1..139

data class Cell(val x: Int, val y: Int)

data class CellRange(val xRange: IntRange, val y: Int) {
    private val adjacentXRange: IntRange
        get() = (xRange.first - 1).coerceAtLeast(XY_RANGE.first())..(xRange.last + 1).coerceAtMost(XY_RANGE.last)

    private val adjacentYRange: IntRange
        get() = (y - 1).coerceAtLeast(XY_RANGE.first())..(y + 1).coerceAtMost(XY_RANGE.last)

    val cells: Set<Cell>
        get() = xRange.map { x -> Cell(x, y) }.toSet()

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

fun MatchResult.toCellRange(lineNumber: Int) = CellRange(range, lineNumber)

fun MatchResult.toNumber(lineNumber: Int) = Number(toCellRange(lineNumber), value.toInt())

fun MatchResult.toSymbol(lineNumber: Int) = Symbol(toCellRange(lineNumber), value.first())

fun List<String>.findNumbers() = flatMapIndexed { lineNumber, line ->
    "\\d+".toRegex().findAll(line).map { it.toNumber(lineNumber) }
}.toSet()

fun List<String>.findSymbols() = flatMapIndexed { lineNumber, line ->
    "[^\\w.]".toRegex().findAll(line).map { it.toSymbol(lineNumber) }
}.toSet()

fun Set<Number>.filterByNeighborsOf(reference: Set<Cell>) =
    filter { it.cellRange.neighbors.intersect(reference).isNotEmpty() }.toSet()

fun Set<Number>.findRealParts(symbols: Set<Symbol>) =
    filterByNeighborsOf(symbols.flatMap { it.cellRange.cells }.toSet())

data class Gear(val partPair: Pair<Int, Int>) {
    val ratio: Int
        get() = partPair.first * partPair.second
}

fun Set<Symbol>.findGears(realParts: Set<Number>): Set<Gear> =
    filter { it.value == '*' }
        .flatMap { it.cellRange.cells }
        .flatMap { starCell ->
            realParts.filter { starCell in it.cellRange.neighbors }
                .takeIf { it.size == 2 }
                ?.let { setOf(Gear(it.first().value to it.last().value)) } ?: emptySet()
        }
        .toSet()