package main

import java.lang.IllegalArgumentException

fun main() {
    val input = readInput(
//        "Day10"
        "Day10_test"
    )



    val field = input.toField()

    val path = field.buildPath()


    val newInput = StringBuilder()

    input.forEachIndexed { lineNumber, line ->
        line.forEachIndexed { pos, char ->
            val tile = if (char == 'S') field.start else Tile(char, Cell(pos, lineNumber))

            if (tile in path) {
                val newChar = when (tile.char) {
                    'L' -> '╚'
                    'J' -> '╝'
                    '7' -> '╗'
                    'F' -> '╔'
                    '-' -> '═'
                    '|' -> '║'
                    'S' -> 'o'
                    else -> throw IllegalArgumentException("invalid pipe")
                }
                newInput.append(newChar)
            } else {
                newInput.append(".")
            }
        }
        newInput.append("\n")
    }

    newInput.println()



    val part1 = path.size / 2
    part1.println()

    val part2 = field.buildEnclosed(path).size
    part2.println()

}



fun Field.buildEnclosed(path: Set<Tile>): Set<Cell> {
    val verticalEdges = path.filterNot { it.char == '-' || it.char == '7' || it.char == 'F' }
    val pathCells = path.map { it.cell }.toSet()
    val xMax = pathCells.maxOfOrNull { it.x }!!
    val xMin = pathCells.minOfOrNull { it.x }!!
    val yMax = pathCells.maxOfOrNull { it.y }!!
    val yMin = pathCells.minOfOrNull { it.y }!!
    return buildSet<Cell> {
        for (x in xMin..xMax) {
            for (y in yMin..yMax) {
                val cell = Cell(x,y)
                if ( cell in pathCells) {
                    continue
                }
                val crossings = verticalEdges
                    .map { it.cell }
                    .count { it.y == y && it.x < x }
                if (crossings % 2 != 0) {
                    add(cell)
                }
            }
        }
    }
}

fun Field.buildPath(): Set<Tile> {
    val startSteps = mutableSetOf<Step>()
    pipes.asSequence().filter {
        start.cell.neighbors.contains(it.cell)
    }.forEach { pipe ->
        val directions = Pipe.getByChar(pipe.char)!!
            .directions.filterNot { navigate(Step(pipe, it)) == start }
        if (directions.size == 1) {
            startSteps.add(Step(pipe, directions.first()))
        }
    }
    var (pipe1, direction1) = startSteps.first()
    var (pipe2, direction2) = startSteps.last()

    val pathTiles = mutableSetOf<Tile>(start, pipe1, pipe2)

    while (true) {
        pipe1 = navigate(Step(pipe1, direction1))!!
        pipe2 = navigate(Step(pipe2, direction2))!!
        pathTiles.add(pipe1)
        if (!pathTiles.add(pipe2)) return pathTiles
        val nextPipe1 = Pipe.getByChar(pipe1.char)!!
        direction1 = nextPipe1.directions.first { it != Direction.getOppositeOf(direction1) }
        val nextPipe2 = Pipe.getByChar(pipe2.char)!!
        direction2 = nextPipe2.directions.first { it != Direction.getOppositeOf(direction2) }
    }
}

data class Step(val from: Tile, val direction: Direction)

fun Field.navigate(step: Step): Tile? {
    val tiles = (pipes union grounds union setOf(start)).associateBy { it.cell }
    val x = step.from.cell.x
    val y = step.from.cell.y
    return when (step.direction) {
        Direction.NORTH -> tiles[Cell(x,y-1)]
        Direction.EAST -> tiles[Cell(x+1,y)]
        Direction.SOUTH -> tiles[Cell(x,y+1)]
        Direction.WEST -> tiles[Cell(x-1,y)]
    }
}

data class Field(val start: Tile, var pipes: Set<Tile>, val grounds: Set<Tile>)

data class Tile(var char: Char, val cell: Cell)

fun List<String>.toField(): Field {
    var start = Tile('S', Cell(-1,-1))
    val pipes: MutableSet<Tile> = mutableSetOf()
    val grounds: MutableSet<Tile> = mutableSetOf()
    forEachIndexed { lineNumber, line ->
        line.forEachIndexed { pos, char ->
            val tile = Tile(char, Cell(pos, lineNumber))
            when (char) {
                'S' -> start = tile
                '.' -> grounds.add(tile)
                else -> pipes.add(tile)
            }
        }
    }
    return Field(start, pipes, grounds)
}

enum class Direction {
    NORTH, EAST, SOUTH, WEST;

    companion object {
        fun getOppositeOf(direction: Direction): Direction = when(direction) {
            NORTH -> SOUTH
            EAST -> WEST
            SOUTH -> NORTH
            WEST -> EAST
        }
    }
}

enum class Pipe(val char: Char, val directions: Set<Direction>) {
    VERTICAL('|', setOf(Direction.NORTH , Direction.SOUTH)),
    HORIZONTAL('-', setOf(Direction.WEST , Direction.EAST)),
    BEND_NE('L', setOf(Direction.NORTH , Direction.EAST)),
    BEND_NW('J', setOf(Direction.NORTH , Direction.WEST)),
    BEND_SE('F', setOf(Direction.SOUTH , Direction.EAST)),
    BEND_SW('7', setOf(Direction.SOUTH , Direction.WEST));

    companion object {
        fun getByChar(char: Char): Pipe? = Pipe.entries.firstOrNull { it.char == char }
    }
}
