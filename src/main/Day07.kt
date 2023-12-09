package main

fun main() {

    val input = readInput(
        "Day07"
//        "Day07_test"
    )

    val hands = input.map { it.toHand() }
    hands.getTotalWinnings().println()
}

const val isJokerInTheHouse = false

fun List<Hand>.getTotalWinnings(): Int = sorted()
    .mapIndexed { index, hand -> hand.bid * (index + 1) }
    .sum()

data class Hand(val camelCards: List<CamelCard>, val bid: Int) : Comparable<Hand> {

    private val handType: HandType by lazy {
        val clusters = camelCards.groupingBy { it }
            .eachCount()
            .toMutableMap()

        if (isJokerInTheHouse) {
            applyJokerRule(clusters)
        }

        val maxCount = clusters.maxOfOrNull { it.value }!!
        val pairCount = clusters.count { it.value == 2 }

        when (maxCount) {
            1 -> HandType.HIGH_CARD
            2 -> if (pairCount == 2) HandType.TWO_PAIR else HandType.ONE_PAIR
            3 -> if (pairCount == 1) HandType.FULL_HOUSE else HandType.THREE_OF_A_KIND
            4 -> HandType.FOUR_OF_A_KIND
            else -> HandType.FIVE_OF_A_KIND
        }
    }

    override fun compareTo(other: Hand): Int =
        if (this.handType == other.handType) {
            this.camelCards.zip(other.camelCards) { thisCard, otherCard ->
                thisCard.value.compareTo(otherCard.value)
            }.firstOrNull { it != 0 } ?: 0
        } else {
            other.handType.compareTo(this.handType)
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

enum class CamelCard(val label: Char, val value: Int) {
    JOKER('J', 1),
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
        fun getByLabel(label: Char) : CamelCard? {
            val ignored = if (isJokerInTheHouse) 0 else 1
            return CamelCard.entries
                .drop(ignored)
                .firstOrNull { it.label == label }
        }
    }
}
