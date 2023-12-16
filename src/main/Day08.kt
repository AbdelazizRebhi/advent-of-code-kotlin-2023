package main

suspend fun main() {
    val input = readInput(
        "Day08"
//        "Day08_test"
    )

    val instructions = input[0].map { Instruction(it) }
    val network = input.drop(2).toNetwork()

    val part1 = network.countStepsToZZZ("AAA", instructions)
    part1.println()

    val part2 = network.ghosts.parallelMap { ghost ->
        network.countStepsToGhostZ(ghost, instructions)
    }.fold(1L) { acc, l -> lcm(acc, l) }

    part2.println()
}

fun Network.countStepsToZZZ(start: String, instructions: List<Instruction>): Long =
    countStepsUntil(start, instructions) { it != "ZZZ"}

fun Network.countStepsToGhostZ(ghost: String, instructions: List<Instruction>): Long =
    countStepsUntil(ghost, instructions) { !it.endsWith("Z") }

inline fun Network.countStepsUntil(
    start: String,
    instructions: List<Instruction>,
    crossinline condition: (String) -> Boolean
): Long {
    var current = start
    var stepCount = 0L
    while (condition(current)) {
        val index = (stepCount++ % instructions.size).toInt()
        current = navigateFrom(current, instructions[index])
    }
    return stepCount
}

data class Network(val nodes: Map<String, Pair<String, String>>) {
    val ghosts: List<String>
        get() = nodes.keys.filter { it.endsWith("A") }
}

suspend fun List<String>.toNetwork() = Network(
    parallelMap { it.split(Regex("\\W+")) }
        .associate { it[0] to (it[1] to it[2]) }
)

data class Instruction(val letter: Char)

fun Network.navigateFrom(start: String, instruction: Instruction): String =
        if (instruction.letter == 'L') {
            nodes[start]!!.first
        } else {
            nodes[start]!!.second
        }
