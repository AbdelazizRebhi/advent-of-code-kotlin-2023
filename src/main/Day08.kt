package main

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {

    val input = readInput(
        "Day08"
//        "Day08_test"
    )

    val instructions = input[0].map { Instruction(it) }
    instructions.println()

    val network = input.subList(2, input.size).toNetwork()


    val part1 = network.countStepsToZZZ("AAA", instructions)

    part1.println()

    val ghosts = network.ghosts

    val part2 = ghosts.parallelMap { ghost ->
        network.countStepsToGhostZ(ghost, instructions)
    }.fold(1L) { acc, l -> lcm(acc, l) }

    part2.println()

}

suspend fun Network.countStepsToZZZ(start: String, instructions: List<Instruction>): Long =
    generateRepeatingSteps(start, instructions) { current -> current != "ZZZ"}

suspend fun Network.countStepsToGhostZ(ghost: String, instructions: List<Instruction>): Long =
    generateRepeatingSteps(ghost, instructions) { current -> !current.endsWith("Z") }

suspend inline fun Network.generateRepeatingSteps(
    start: String,
    instructions: List<Instruction>,
    crossinline condition: (String) -> Boolean
): Long {
    var current = start
    var stepCount = 0L

    instructions.generateRepeating()
        .takeWhile { condition(current) }
        .collect {
            current = navigateFrom(current, it)
            stepCount++
        }

    return stepCount
}

fun List<Instruction>.generateRepeating(): Flow<Instruction> = flow {
    while (true) {
        forEach {
            emit(it)
        }
    }
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









