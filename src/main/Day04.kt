package main

import kotlin.math.pow

fun main() {

    val input = readInput(
        "Day04"
//        "Day04_test"
    )

    val cards = input.map { it.toCard() }

    val cardCount = IntArray(input.size) { 1 }

    cards.map { it.getWinCount() }.forEachIndexed { cardId, winCount ->
        (1..winCount).forEach { cardCount[cardId + it] += cardCount[cardId] }
    }

    val part1 = cards.sumOf { it.getPoints() }
    val part2 = cardCount.sum()

    part1.println()
    part2.println()
}

data class Card(val id: Int, val targetNumbers: Set<Int>, val playerNumbers: Set<Int>)

fun Card.getWinCount(): Int = (targetNumbers intersect playerNumbers).size

fun Card.getPoints(): Int = 2.0.pow(getWinCount() - 1).toInt()

fun String.toCardId(): Int =
    substringBefore(":").split(Regex("\\s+")).last().toInt()

fun String.toTargetNumbers(): Set<Int> =
    substringAfter(":").substringBefore("|")
        .trim().split(Regex("\\s+")).map(String::toInt).toSet()

fun String.toPlayerNumbers(): Set<Int> =
    substringAfter("|")
        .trim().split(Regex("\\s+")).map(String::toInt).toSet()

fun String.toCard() = Card(toCardId(), toTargetNumbers(), toPlayerNumbers())