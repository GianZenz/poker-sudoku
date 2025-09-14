package com.pokersudoku.game.model

/**
 * Represents a playing card with suit and rank
 */
data class Card(
    val suit: Suit,
    val rank: Rank
) {
    /**
     * Get the display text for the card
     */
    fun getDisplayText(): String = "${rank.symbol}${suit.symbol}"
    
    /**
     * Check if this card is equal to another card (ignoring suit for Sudoku rules)
     * In Poker Sudoku, cards are considered equal if they have the same rank
     */
    fun isSameRank(other: Card): Boolean = this.rank == other.rank
    
    override fun toString(): String = getDisplayText()
}

/**
 * Card suits with their symbols and colors
 */
enum class Suit(val symbol: String, val isRed: Boolean) {
    HEARTS("♥", true),
    DIAMONDS("♦", true),
    CLUBS("♣", false),
    SPADES("♠", false);
    
    companion object {
        val all = values().toList()
    }
}

/**
 * Card ranks with their symbols and numeric values for Sudoku
 */
enum class Rank(val symbol: String, val value: Int) {
    ACE("A", 1),
    TWO("2", 2),
    THREE("3", 3),
    FOUR("4", 4),
    FIVE("5", 5),
    SIX("6", 6),
    SEVEN("7", 7),
    EIGHT("8", 8),
    NINE("9", 9),
    TEN("10", 10),
    JACK("J", 11),
    QUEEN("Q", 12),
    KING("K", 13);
    
    companion object {
        val all = values().toList()
        // For Sudoku, we'll use ranks 1-9 (Ace through 9)
        val sudokuRanks = listOf(ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE)
    }
}

/**
 * Companion object for Card with utility functions
 */
object CardFactory {
    /**
     * Create all 52 standard playing cards
     */
    fun createFullDeck(): List<Card> {
        return Suit.all.flatMap { suit ->
            Rank.all.map { rank ->
                Card(suit, rank)
            }
        }
    }
    
    /**
     * Create a subset of cards for Sudoku (Ace through 9, one of each suit)
     */
    fun createSudokuCards(): List<Card> {
        return Suit.all.flatMap { suit ->
            Rank.sudokuRanks.map { rank ->
                Card(suit, rank)
            }
        }
    }
    
    /**
     * Get a random card from the Sudoku set
     */
    fun getRandomSudokuCard(): Card {
        val cards = createSudokuCards()
        return cards.random()
    }
}
