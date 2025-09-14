package com.pokersudoku.game.logic

import com.pokersudoku.game.model.*
import kotlin.random.Random

/**
 * Core Sudoku logic adapted for poker cards
 */
object SudokuLogic {
    
    /**
     * Generate a valid Sudoku puzzle with poker cards
     */
    fun generatePuzzle(difficulty: Difficulty): List<List<CellState>> {
        // Start with a solved grid
        val solvedGrid = generateSolvedGrid()
        
        // Create a copy and remove cells based on difficulty
        val puzzle = solvedGrid.map { row ->
            row.map { cell -> cell.copy(isGiven = true) }
        }
        
        val cellsToRemove = when (difficulty) {
            Difficulty.EASY -> 35..40
            Difficulty.MEDIUM -> 41..46
            Difficulty.HARD -> 47..52
            Difficulty.EXPERT -> 30..35 // Fewer cells removed for expert (suits make it harder)
        }
        
        val numberOfCellsToRemove = Random.nextInt(cellsToRemove.first, cellsToRemove.last + 1)
        val positions = mutableSetOf<Pair<Int, Int>>()
        
        // Randomly select positions to clear
        while (positions.size < numberOfCellsToRemove) {
            val row = Random.nextInt(0, GameState.GRID_SIZE)
            val col = Random.nextInt(0, GameState.GRID_SIZE)
            positions.add(Pair(row, col))
        }
        
        // Clear the selected positions
        return puzzle.mapIndexed { rowIndex, row ->
            row.mapIndexed { colIndex, cell ->
                if (positions.contains(Pair(rowIndex, colIndex))) {
                    CellState(isGiven = false)
                } else {
                    cell
                }
            }
        }
    }
    
    /**
     * Generate a completely solved Sudoku grid using poker cards
     */
    fun generateSolvedGrid(): List<List<CellState>> {
        val grid = GameState.createEmptyGrid()
        
        // Fill the first row with cards Ace through 9
        val firstRowCards = CardFactory.createSudokuCards().take(9)
        val filledGrid = grid.mapIndexed { rowIndex, row ->
            if (rowIndex == 0) {
                row.mapIndexed { colIndex, _ ->
                    CellState(card = firstRowCards[colIndex], isGiven = true)
                }
            } else {
                row
            }
        }
        
        // Use backtracking to fill the rest (only for non-Expert mode for now)
        return solveGrid(filledGrid) ?: grid
    }
    
    /**
     * Solve a Sudoku grid using backtracking algorithm
     */
    private fun solveGrid(grid: List<List<CellState>>): List<List<CellState>>? {
        for (row in 0 until GameState.GRID_SIZE) {
            for (col in 0 until GameState.GRID_SIZE) {
                if (grid[row][col].card == null) {
                    val availableCards = getAvailableCards(grid, row, col)
                    
                    for (card in availableCards.shuffled()) {
                        val newGrid = grid.mapIndexed { r, rowList ->
                            rowList.mapIndexed { c, cell ->
                                if (r == row && c == col) {
                                    cell.copy(card = card)
                                } else {
                                    cell
                                }
                            }
                        }
                        
                        val result = solveGrid(newGrid)
                        if (result != null) {
                            return result
                        }
                    }
                    return null // No valid card found for this position
                }
            }
        }
        return grid // Grid is completely filled
    }
    
    /**
     * Get cards that can be legally placed at the given position
     */
    fun getAvailableCards(grid: List<List<CellState>>, row: Int, col: Int): List<Card> {
        val usedInRow = getUsedCardsInRow(grid, row)
        val usedInColumn = getUsedCardsInColumn(grid, col)
        val usedInBox = getUsedCardsInBox(grid, row, col)
        
        val allUsedCards = (usedInRow + usedInColumn + usedInBox).toSet()
        
        return CardFactory.createSudokuCards().filter { card ->
            !allUsedCards.any { usedCard -> usedCard.isSameRank(card) }
        }
    }
    
    /**
     * Get all cards used in a specific row
     */
    private fun getUsedCardsInRow(grid: List<List<CellState>>, row: Int): List<Card> {
        return grid[row].mapNotNull { it.card }
    }
    
    /**
     * Get all cards used in a specific column
     */
    private fun getUsedCardsInColumn(grid: List<List<CellState>>, col: Int): List<Card> {
        return grid.mapNotNull { row -> row[col].card }
    }
    
    /**
     * Get all cards used in a specific 3x3 box
     */
    private fun getUsedCardsInBox(grid: List<List<CellState>>, row: Int, col: Int): List<Card> {
        val boxRow = (row / GameState.BOX_SIZE) * GameState.BOX_SIZE
        val boxCol = (col / GameState.BOX_SIZE) * GameState.BOX_SIZE
        
        val usedCards = mutableListOf<Card>()
        for (r in boxRow until boxRow + GameState.BOX_SIZE) {
            for (c in boxCol until boxCol + GameState.BOX_SIZE) {
                grid[r][c].card?.let { usedCards.add(it) }
            }
        }
        return usedCards
    }
    
    /**
     * Check if a card can be placed at the given position
     */
    fun canPlaceCard(grid: List<List<CellState>>, row: Int, col: Int, card: Card, difficulty: Difficulty = Difficulty.HARD): Boolean {
        // Check if the position is already filled with a given card
        if (grid[row][col].isGiven) return false
        
        // Check for conflicts in row, column, and box
        val hasConflict = hasConflictAtPosition(grid, row, col, card, difficulty)
        return !hasConflict
    }
    
    /**
     * Check if placing a card at the given position would create a conflict
     */
    fun hasConflictAtPosition(grid: List<List<CellState>>, row: Int, col: Int, card: Card, difficulty: Difficulty = Difficulty.HARD): Boolean {
        // Check row conflict (rank)
        for (c in 0 until GameState.GRID_SIZE) {
            if (c != col) {
                grid[row][c].card?.let { existingCard ->
                    if (existingCard.isSameRank(card)) return true
                    // Expert mode: also check for suit conflicts
                    if (difficulty == Difficulty.EXPERT && existingCard.suit == card.suit) return true
                }
            }
        }
        
        // Check column conflict (rank)
        for (r in 0 until GameState.GRID_SIZE) {
            if (r != row) {
                grid[r][col].card?.let { existingCard ->
                    if (existingCard.isSameRank(card)) return true
                    // Expert mode: also check for suit conflicts
                    if (difficulty == Difficulty.EXPERT && existingCard.suit == card.suit) return true
                }
            }
        }
        
        // Check box conflict (rank)
        val boxRow = (row / GameState.BOX_SIZE) * GameState.BOX_SIZE
        val boxCol = (col / GameState.BOX_SIZE) * GameState.BOX_SIZE
        
        for (r in boxRow until boxRow + GameState.BOX_SIZE) {
            for (c in boxCol until boxCol + GameState.BOX_SIZE) {
                if (r != row || c != col) {
                    grid[r][c].card?.let { existingCard ->
                        if (existingCard.isSameRank(card)) return true
                        // Expert mode: also check for suit conflicts
                        if (difficulty == Difficulty.EXPERT && existingCard.suit == card.suit) return true
                    }
                }
            }
        }
        
        return false
    }
    
    /**
     * Check if the current grid is a valid solution
     */
    fun isValidSolution(grid: List<List<CellState>>): Boolean {
        // Check if all cells are filled
        for (row in grid) {
            for (cell in row) {
                if (cell.card == null) return false
            }
        }
        
        // Check each row, column, and box for duplicates
        for (i in 0 until GameState.GRID_SIZE) {
            if (!isValidRow(grid, i) || !isValidColumn(grid, i)) {
                return false
            }
        }
        
        // Check each 3x3 box
        for (boxRow in 0 until GameState.BOX_SIZE) {
            for (boxCol in 0 until GameState.BOX_SIZE) {
                if (!isValidBox(grid, boxRow * GameState.BOX_SIZE, boxCol * GameState.BOX_SIZE)) {
                    return false
                }
            }
        }
        
        return true
    }
    
    /**
     * Check if a row is valid (no duplicate ranks)
     */
    private fun isValidRow(grid: List<List<CellState>>, row: Int): Boolean {
        val cards = grid[row].mapNotNull { it.card }
        return cards.size == GameState.GRID_SIZE && cards.distinctBy { it.rank }.size == GameState.GRID_SIZE
    }
    
    /**
     * Check if a column is valid (no duplicate ranks)
     */
    private fun isValidColumn(grid: List<List<CellState>>, col: Int): Boolean {
        val cards = grid.mapNotNull { row -> row[col].card }
        return cards.size == GameState.GRID_SIZE && cards.distinctBy { it.rank }.size == GameState.GRID_SIZE
    }
    
    /**
     * Check if a 3x3 box is valid (no duplicate ranks)
     */
    private fun isValidBox(grid: List<List<CellState>>, startRow: Int, startCol: Int): Boolean {
        val cards = mutableListOf<Card>()
        for (r in startRow until startRow + GameState.BOX_SIZE) {
            for (c in startCol until startCol + GameState.BOX_SIZE) {
                grid[r][c].card?.let { cards.add(it) }
            }
        }
        return cards.size == 9 && cards.distinctBy { it.rank }.size == 9
    }
    
    /**
     * Find all conflicts in the current grid
     */
    fun findConflicts(grid: List<List<CellState>>, difficulty: Difficulty = Difficulty.HARD): List<Conflict> {
        val conflicts = mutableListOf<Conflict>()
        
        for (row in 0 until GameState.GRID_SIZE) {
            for (col in 0 until GameState.GRID_SIZE) {
                grid[row][col].card?.let { card ->
                    // Check row conflicts
                    for (c in 0 until GameState.GRID_SIZE) {
                        if (c != col) {
                            grid[row][c].card?.let { otherCard ->
                                if (card.isSameRank(otherCard) || 
                                    (difficulty == Difficulty.EXPERT && card.suit == otherCard.suit)) {
                                    conflicts.add(Conflict(Pair(row, col), ConflictType.ROW_CONFLICT))
                                    return@let
                                }
                            }
                        }
                    }
                    
                    // Check column conflicts
                    for (r in 0 until GameState.GRID_SIZE) {
                        if (r != row) {
                            grid[r][col].card?.let { otherCard ->
                                if (card.isSameRank(otherCard) || 
                                    (difficulty == Difficulty.EXPERT && card.suit == otherCard.suit)) {
                                    conflicts.add(Conflict(Pair(row, col), ConflictType.COLUMN_CONFLICT))
                                    return@let
                                }
                            }
                        }
                    }
                    
                    // Check box conflicts
                    val boxRow = (row / GameState.BOX_SIZE) * GameState.BOX_SIZE
                    val boxCol = (col / GameState.BOX_SIZE) * GameState.BOX_SIZE
                    
                    for (r in boxRow until boxRow + GameState.BOX_SIZE) {
                        for (c in boxCol until boxCol + GameState.BOX_SIZE) {
                            if (r != row || c != col) {
                                grid[r][c].card?.let { otherCard ->
                                    if (card.isSameRank(otherCard) || 
                                        (difficulty == Difficulty.EXPERT && card.suit == otherCard.suit)) {
                                        conflicts.add(Conflict(Pair(row, col), ConflictType.BOX_CONFLICT))
                                        return@let
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        return conflicts
    }
}
