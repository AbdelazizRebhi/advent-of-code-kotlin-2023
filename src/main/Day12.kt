package main

fun main() {
    val input = readInput(
        "Day12"
//        "Day12_test"
    )

    input.sumOf { it.toConditionRecord().countArrangements() }.println()

    input.sumOf { it.toConditionRecord().unfold(5).countArrangements() }.println()
}

fun ConditionRecord.countArrangements(): Long {
    val g = groups.size
    val s = springs.length
    val cache = Array(s + 1) { arrayOfNulls<Long>(g + 1) }
    val (damaged, blocks) = springs.extractDamagedAndBlocks()

    fun count(pos: Int, remaining: Int): Long {
        // we placed all damaged groups, are there damaged springs to the right?
        if (remaining == 0) return if (damaged.count { it >= pos } == 0) 1 else 0

        // we reached the end and there are still damaged springs to place
        if (pos >= s) return 0

        cache[pos][remaining]?.let { return it }

        val currentBlock = blocks.firstOrNull { it.second >= pos }
        if (currentBlock == null) {
            cache[pos][remaining] = 0
            return 0
        }

        if (pos < currentBlock.first) {
            return count(currentBlock.first, remaining)
        }

        val end = pos + groups[g - remaining] - 1
        val isPlaceable = end <= currentBlock.second && (end + 1) !in damaged

        // if current spring is damaged (#) we have to place a group
        if (pos in damaged && !isPlaceable) {
            cache[pos][remaining] = 0
            return 0
        }

        var sum = 0L
        // place group from pos to end, end + 1 must be operational, count arrangements from end + 2
        if (isPlaceable) {
            sum += count(end + 2, remaining - 1)
        }

        // place an operational spring and count arrangements from pos + 1
        if (pos !in damaged) {
            sum += count(pos + 1, remaining)
        }

        cache[pos][remaining] = sum
        return sum
    }

    return count(0, g)
}

fun String.extractDamagedAndBlocks(): Pair<Set<Int>, List<Pair<Int, Int>>> {
    val damaged = mutableSetOf<Int>()
    val blocks = mutableListOf<Pair<Int, Int>>()
    var blockStart = -1
    forEachIndexed { index, spring ->
        if (spring == '#' || spring == '?') {
            if (blockStart == -1) {
                blockStart = index
            }
            if (spring == '#') {
                damaged.add(index)
            }
        } else if (blockStart != -1) {
            blocks.add(blockStart to index - 1)
            blockStart = -1
        }
    }
    if (blockStart != -1) {
        blocks.add(blockStart to length - 1)
    }
    return damaged to blocks
}

fun ConditionRecord.unfold(times: Int) = apply {
    springs = List(times) { springs }.joinToString("?")
    groups = List(times) { groups }.flatten()
}

fun String.toConditionRecord() = ConditionRecord(
    substringBefore(" "),
    substringAfter(" ").split(",").map { it.toInt() }
)

data class ConditionRecord(var springs: String, var groups: List<Int>)