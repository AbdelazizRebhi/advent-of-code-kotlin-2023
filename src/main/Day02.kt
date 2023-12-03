package main

fun main() {
    fun part1(input: List<String>): Int = input
        .map(String::toGame)
        .filter(Game::isPossible)
        .sumOf(Game::id)

    fun part2(input: List<String>): Int = input
        .map(String::toGame)
        .sumOf(Game::power)

    val input = readInput(
        "Day02"
//        "Day02_test"
    )

    part1(input).println()

    part2(input).println()
}

enum class Color { RED, GREEN, BLUE }

data class Cube(val color: Color, val number: Int)
fun String.toCube() = Cube (
    Color.valueOf(this.substringAfter(" ").uppercase()),
    this.substringBefore(" ").toInt()
)
data class Turn(val cubes: List<Cube>) {
    operator fun get(color: Color): Int {
        return cubes.associateBy { it.color }[color]?.number ?: 0
    }
}

fun String.toTurn() = Turn (
    split(", ").map(String::toCube)
)

data class Game(val id: Int, val turns: List<Turn>) {
    operator fun get(color: Color) = turns.maxByOrNull { it[color] }!![color]

    fun isPossible(): Boolean {
        return (this[Color.RED] < 13) && (this[Color.GREEN] < 14) && (this[Color.BLUE] < 15)
    }
    val power: Int
        get() = this[Color.RED] * this[Color.GREEN] * this[Color.BLUE]
}

fun String.toGame() = Game (
    substringBefore(": ").substringAfter(" ").toInt(),
    substringAfter(": ").split("; ").map(String::toTurn)
)