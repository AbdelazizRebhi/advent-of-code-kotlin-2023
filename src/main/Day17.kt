package main

import java.util.Comparator
import java.util.PriorityQueue

fun main() {
    val input = readInput(
        "Day17"
//        "Day17_test"
    )

    input.minimalHeatLoss(0..3).println()
    input.minimalHeatLoss(4..10).println()
}

fun List<String>.minimalHeatLoss(straightRange: IntRange): Long {
    val (xMax, yMax) = first().length - 1 to size - 1
    val (start, goal) = Cell(0,0) to Cell(xMax, yMax)

    val costComparator: Comparator<CityBlock> = compareBy { it.loss }
    val queue = PriorityQueue(costComparator)

    val startNode = Node(start, Direction.RIGHT, 0)
    queue.add(CityBlock(startNode, 0L))
    val visited = mutableSetOf(startNode)

    fun CityBlock.getNeighbors(): Sequence<CityBlock> {
        return Direction.entries.asSequence()
            // crucible can't reverse
            .filterNot {
                it.ordinal == (node.direction.ordinal + 2) % 4
            }
            // crucible has to move forward
            .filterNot {
                node.straightCount in 1 until straightRange.first && it != node.direction
            }
            // crucible has to turn
            .filterNot {
                node.straightCount == straightRange.last && it == node.direction
            }.filter {
                node.cell.move(it).x in 0..xMax
                        && node.cell.move(it).y in 0..yMax
            }.map {
                val nextCell = node.cell.move(it)
                val nextLoss = this@minimalHeatLoss[nextCell.y][nextCell.x].digitToInt()
                val nextCount = if (it == node.direction) node.straightCount + 1 else 1
                val nextNode = Node(nextCell, it, nextCount)
                CityBlock(nextNode, loss + nextLoss)
            }
    }

    while (queue.isNotEmpty()) {
        val current = queue.poll()
        if (current.node.cell == goal && current.node.straightCount in straightRange)
            return current.loss

        current.getNeighbors()
            .filter { visited.add(it.node) }
            .forEach(queue::add)
    }

    return Long.MAX_VALUE
}

data class Node(
    val cell: Cell,
    val direction: Direction,
    val straightCount: Int,
)

data class CityBlock(
    val node: Node,
    val loss: Long,
)
