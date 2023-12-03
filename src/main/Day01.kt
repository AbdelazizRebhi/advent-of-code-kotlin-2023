package main

fun main() {
    fun part1(input: List<String>): Int = input.sumOf {
        line -> findNumericDigitsInLine(line).toCalibrationValue()
    }

    fun part2(input: List<String>): Int = input.sumOf {
        line -> findValidDigitsInLine(line).toCalibrationValue()
    }

    val input = readInput(
        "Day01"
//        "Day01_test"
    )

    part1(input).println()

    part2(input).println()
}

// Valid digits = worded digits +  numeric digits

fun findNumericDigitsInLine(line: String) = "\\d".toRegex()
    .findAll(line)
    .map(MatchResult::value)
    .map(String::toInt)
    .toList()

fun List<Int>.toCalibrationValue() = this.first() * 10 + this.last()

enum class WordedDigit {
    ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE,
}

fun String.validDigitToInt(): Int = if (length > 1) {
    WordedDigit.valueOf(this.uppercase()).ordinal + 1
} else {
    toInt()
}

fun MatchResult.toPair(): Pair<Int, Int> = this.range.first to this.value.validDigitToInt()

val validDigitRegex = buildList {
    add("\\d")
    addAll(WordedDigit.entries.map { it.name.lowercase() })
}.map(String::toRegex)

// all occurrences of valid digits in a single line
// as list of pairs: (index, digit), sorted by the index
fun findValidDigitsInLine(line: String) = validDigitRegex.flatMap { regex ->
    regex.findAll(line).map(MatchResult::toPair)
}.toList().sortedBy(Pair<Int, Int>::first)

fun List<Pair<Int, Int>>.toCalibrationValue() = this.first().second * 10 + this.last().second



