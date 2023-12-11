package main

fun main() {

    val input = readInput(
//        "Day08"
        "Day08_test"
    )




    val part1 = input.size

    part1.println()

    val part2 = input.size

    part2.println()


}

data class Instruction(val letter: Char, val operation: (Pair<String, String>) -> String)