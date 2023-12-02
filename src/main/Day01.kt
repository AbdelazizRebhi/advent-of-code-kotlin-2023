package main

fun main() {
    fun part1(input: List<String>): Int {
        var sum1 = 0

        input.forEach {
            val digits = "\\d".toRegex().findAll(it).map(MatchResult::value).toList()

            sum1 += (digits.first() + digits.last()).toInt()
        }

        return sum1
    }

    fun part2(input: List<String>): Int {
        var sum2 = 0

        val digitRegex = buildList {
            add("\\d")
            addAll(Digit.entries.map { it.name.lowercase() })
        }.map(String::toRegex)

        input.forEach { line ->
            val lineMatches = digitRegex.flatMap { regex ->
                regex.findAll(line).map(MatchResult::toPair)
            }.toList().sortedBy(Pair<Int, Int>::first)

            sum2 += lineMatches.first().second * 10 + lineMatches.last().second
        }

        return sum2
    }




    val input = readInput(
        "Day01"
//        "Day01_test"
    )

    part1(input).println()

    part2(input).println()
}


