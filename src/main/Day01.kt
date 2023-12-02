package main

fun main() {
    fun part1(input: List<String>): Int {
        var sum = 0
        val regex = """\d""".toRegex()
        input.forEach {
            val digits = regex
                .findAll(it)
                .map(MatchResult::value)
                .toList()

            sum += (digits.first() + digits.last()).toInt()
        }
        return sum
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    val input = readInput(
        "Day01"
//        "Day01_test"
    )

    part1(input).println()

    //part2(input).println()
}
