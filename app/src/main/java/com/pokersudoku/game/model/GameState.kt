package com.pokersudoku.game.model

/**
 * Represents the state of a single cell in the Sudoku grid
 */
data class CellState(
    val card: Card? = null,
    val isGiven: Boolean = false, // True if this cell was pre-filled (part of the puzzle)
    val isSelected: Boolean = false,
    val hasConflict: Boolean = false
)

/**
 * Represents the difficulty level of the game
 */
enum class Difficulty {
    EASY,
    MEDIUM,
    HARD,
    EXPERT // New difficulty where suits matter
}

/**
 * Represents the current state of the Poker Sudoku game
 */
data class GameState(
    val grid: List<List<CellState>> = createEmptyGrid(),
    val selectedCard: Card? = null,
    val selectedCell: Pair<Int, Int>? = null,
    val difficulty: Difficulty = Difficulty.EASY,
    val isGameComplete: Boolean = false,
    val isGameWon: Boolean = false,
    val timeElapsed: Long = 0L, // in seconds
    val moveCount: Int = 0,
    val hintsUsed: Int = 0
) {
    companion object {
        const val GRID_SIZE = 9
        const val BOX_SIZE = 3
        
        fun createEmptyGrid(): List<List<CellState>> {
            return List(GRID_SIZE) { 
                List(GRID_SIZE) { CellState() }
            }
        }
    }
}

/**
 * Represents the result of a game action
 */
sealed class GameActionResult {
    object Success : GameActionResult()
    data class Error(val message: String) : GameActionResult()
    object GameWon : GameActionResult()
}

/**
 * Represents different types of conflicts in the grid
 */
data class Conflict(
    val position: Pair<Int, Int>,
    val conflictType: ConflictType
)

enum class ConflictType {
    ROW_CONFLICT,
    COLUMN_CONFLICT,
    BOX_CONFLICT
}
