package main

fun main() {

    val input = readInput(
        "Day07"
//        "Day07_test"
    )

    val hands = input.map { it.toHand() }

    val part12 = hands.getTotalWinnings()

    part12.println()

}

fun List<Hand>.getTotalWinnings(): Int = sorted()
    .mapIndexed { index, hand -> hand.bid * (index + 1) }
    .sum()

fun Hand.getType(): HandType {
    val clusters = camelCards.groupingBy { it }
        .eachCount()
        .toMutableMap()

    // comment this for part 1
    applyJokerRule(clusters)

    val maxCount = clusters.maxByOrNull { it.value }!!.value

    return when (maxCount) {
        1 -> HandType.HIGH_CARD
        2 -> if (clusters.count { it.value == 2 } > 1) {
            HandType.TWO_PAIR
        } else {
            HandType.ONE_PAIR
        }
        3 -> if (clusters.count { it.value == 2 } == 1 ) {
            HandType.FULL_HOUSE
        } else {
            HandType.THREE_OF_A_KIND
        }
        4 -> HandType.FOUR_OF_A_KIND
        else -> HandType.FIVE_OF_A_KIND
    }
}

private fun applyJokerRule(clusters: MutableMap<CamelCard, Int>) {
    val joker = CamelCard.entries.first()
    val jokerCount = clusters[joker] ?: 0
    clusters.filterNot { it.key == joker }
        .maxByOrNull { it.value }?.let {
            clusters.merge(it.key, jokerCount, Int::plus)
            clusters.remove(joker)
        }
}

enum class HandType {
    FIVE_OF_A_KIND,
    FOUR_OF_A_KIND,
    FULL_HOUSE,
    THREE_OF_A_KIND,
    TWO_PAIR,
    ONE_PAIR,
    HIGH_CARD,
}

fun String.toHand() = Hand(
    substringBefore(" ").map { CamelCard.getByLabel(it)!! },
    substringAfter(" ").trim().toInt()
)

data class Hand(val camelCards: List<CamelCard>, val bid: Int) : Comparable<Hand> {
    override fun compareTo(other: Hand): Int =
        if (this.getType() == other.getType()) {
            this.camelCards.zip(other.camelCards) { thisCard, otherCard ->
                thisCard.value.compareTo(otherCard.value)
            }.firstOrNull { it != 0 } ?: 0
        } else {
            other.getType().compareTo(this.getType())
        }
}

enum class CamelCard(val label: Char, val value: Int) {
    JOKER('J', 1), // comment this for part 1
    TWO('2', 2),
    THREE('3', 3),
    FOUR('4', 4),
    FIVE('5', 5),
    SIX('6', 6),
    SEVEN('7', 7),
    EIGHT('8', 8),
    NINE('9', 9),
    TEN('T', 10),
    JACK('J', 11),
    QUEEN('Q', 12),
    KING('K', 13),
    ACE('A', 14);

    companion object {
        fun getByLabel(label: Char) = CamelCard.entries.firstOrNull { it.label == label }
    }
}
