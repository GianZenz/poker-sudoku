package com.pokersudoku.game.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokersudoku.game.logic.SudokuLogic
import com.pokersudoku.game.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing the Poker Sudoku game state
 */
class GameViewModel : ViewModel() {
    
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()
    
    private val _availableCards = MutableStateFlow(CardFactory.createSudokuCards())
    val availableCards: StateFlow<List<Card>> = _availableCards.asStateFlow()
    
    /**
     * Start a new game with the specified difficulty
     */
    fun startNewGame(difficulty: Difficulty = Difficulty.EASY) {
        viewModelScope.launch {
            val puzzle = SudokuLogic.generatePuzzle(difficulty)
            _gameState.value = GameState(
                grid = puzzle,
                difficulty = difficulty,
                isGameComplete = false,
                isGameWon = false,
                timeElapsed = 0L,
                moveCount = 0,
                hintsUsed = 0,
                selectedCell = null,
                selectedCard = null
            )
            // Reset available cards
            _availableCards.value = CardFactory.createSudokuCards()
        }
    }
    
    /**
     * Place a card at the specified position
     * @return String? feedback message if move is invalid, null if move is valid
     */
    fun placeCard(row: Int, col: Int, card: Card): String? {
        val currentState = _gameState.value
        val currentGrid = currentState.grid
        
        // Check if cell is given (part of initial puzzle)
        if (currentGrid[row][col].isGiven) {
            return "Cannot modify initial puzzle cards"
        }
        
        // Check if the position can accept a card
        if (!SudokuLogic.canPlaceCard(currentGrid, row, col, card, currentState.difficulty)) {
            // Check what kind of conflict exists
            val rowConflict = currentGrid[row].any { it.card?.rank == card.rank }
            val colConflict = currentGrid.any { it[col].card?.rank == card.rank }
            val boxConflict = hasBoxConflict(currentGrid, row, col, card)
            
            return when {
                rowConflict -> "This ${card.rank.symbol} already exists in this row"
                colConflict -> "This ${card.rank.symbol} already exists in this column"
                boxConflict -> "This ${card.rank.symbol} already exists in this 3x3 box"
                else -> "Invalid move"
            }
        }
        
        // If we get here, the move is valid
        // Create new grid with the card placed
        val newGrid = currentGrid.mapIndexed { r, rowList ->
            rowList.mapIndexed { c, cell ->
                when {
                    r == row && c == col -> cell.copy(card = card, hasConflict = false)
                    else -> cell.copy(hasConflict = false) // Clear previous conflicts
                }
            }
        }
        
        // Process the valid move and return null to indicate success
        
        // Check for conflicts and update grid
        val conflicts = SudokuLogic.findConflicts(newGrid, currentState.difficulty)
        val finalGrid = newGrid.mapIndexed { r, rowList ->
            rowList.mapIndexed { c, cell ->
                val hasConflict = conflicts.any { it.position == Pair(r, c) }
                cell.copy(hasConflict = hasConflict)
            }
        }
        
        // Check if game is complete
        val isComplete = SudokuLogic.isValidSolution(finalGrid)
        val isWon = isComplete && conflicts.isEmpty()
        
        _gameState.value = currentState.copy(
            grid = finalGrid,
            selectedCell = null,
            moveCount = currentState.moveCount + 1,
            isGameComplete = isComplete,
            isGameWon = isWon
        )
        
        return null // Return null to indicate success
    }
    
    /**
     * Remove a card from the specified position
     */
    fun removeCard(row: Int, col: Int) {
        val currentState = _gameState.value
        val currentGrid = currentState.grid
        
        // Don't allow removing given cards
        if (currentGrid[row][col].isGiven) return
        
        val newGrid = currentGrid.mapIndexed { r, rowList ->
            rowList.mapIndexed { c, cell ->
                when {
                    r == row && c == col -> CellState(isGiven = false)
                    else -> cell.copy(hasConflict = false) // Clear conflicts when removing
                }
            }
        }
        
        // Recalculate conflicts
        val conflicts = SudokuLogic.findConflicts(newGrid, currentState.difficulty)
        val finalGrid = newGrid.mapIndexed { r, rowList ->
            rowList.mapIndexed { c, cell ->
                val hasConflict = conflicts.any { it.position == Pair(r, c) }
                cell.copy(hasConflict = hasConflict)
            }
        }
        
        _gameState.value = currentState.copy(
            grid = finalGrid,
            selectedCell = null,
            moveCount = currentState.moveCount + 1
        )
    }
    
    /**
     * Select a cell in the grid
     */
    fun selectCell(row: Int, col: Int) {
        val currentState = _gameState.value
        val newGrid = currentState.grid.mapIndexed { r, rowList ->
            rowList.mapIndexed { c, cell ->
                cell.copy(isSelected = r == row && c == col)
            }
        }
        
        _gameState.value = currentState.copy(
            grid = newGrid,
            selectedCell = Pair(row, col)
        )
    }
    
    /**
     * Clear cell selection
     */
    fun clearSelection() {
        val currentState = _gameState.value
        val newGrid = currentState.grid.map { row ->
            row.map { cell -> cell.copy(isSelected = false) }
        }
        
        _gameState.value = currentState.copy(
            grid = newGrid,
            selectedCell = null
        )
    }
    
    /**
     * Select a card for placing
     */
    fun selectCard(card: Card) {
        _gameState.value = _gameState.value.copy(selectedCard = card)
    }
    
    /**
     * Clear card selection
     */
    fun clearCardSelection() {
        _gameState.value = _gameState.value.copy(selectedCard = null)
    }
    
    /**
     * Get a hint for the current position
     */
    fun getHint(): String {
        val currentState = _gameState.value
        val selectedCell = currentState.selectedCell
        
        if (selectedCell == null) {
            return "Please select a cell first!"
        }
        
        val row = selectedCell.first
        val col = selectedCell.second
        
        // Don't provide hints for given cells
        if (currentState.grid[row][col].isGiven) {
            return "This cell is pre-filled. Select an empty cell for a hint."
        }
        
        // Find available cards for this position
        val availableCards = SudokuLogic.getAvailableCards(currentState.grid, row, col)
        
        return if (availableCards.isNotEmpty()) {
            val hintCard = availableCards.random()
            _gameState.value = currentState.copy(hintsUsed = currentState.hintsUsed + 1)
            "Hint: Try placing ${hintCard.getDisplayText()} at this position"
        } else {
            "No valid cards can be placed here. Check for conflicts."
        }
    }
    
    /**
     * Check if the current solution is correct
     */
    fun checkSolution(): String {
        val currentState = _gameState.value
        
        // Check if all cells are filled
        val emptyCells = currentState.grid.sumOf { row -> row.count { it.card == null } }
        
        return if (emptyCells > 0) {
            "Puzzle incomplete! ${emptyCells} cells still empty."
        } else {
            val isValid = SudokuLogic.isValidSolution(currentState.grid)
            val conflicts = SudokuLogic.findConflicts(currentState.grid, currentState.difficulty)
            
            if (isValid && conflicts.isEmpty()) {
                "🎉 Congratulations! Solution is correct!"
            } else {
                "❌ Solution has errors. ${conflicts.size} conflicts found."
            }
        }
    }
    
    /**
     * Update the game timer
     */
    fun updateTimer(seconds: Long) {
        _gameState.value = _gameState.value.copy(timeElapsed = seconds)
    }
    
    /**
     * Get available cards for the selected position
     */
    fun getAvailableCardsForPosition(row: Int, col: Int): List<Card> {
        val currentState = _gameState.value
        return getAvailableCardsInternal(currentState.grid, row, col)
    }
    
    /**
     * Get available cards for a position (helper method)
     */
    private fun getAvailableCardsInternal(grid: List<List<CellState>>, row: Int, col: Int): List<Card> {
        val usedInRow = getUsedCardsInRow(grid, row)
        val usedInColumn = getUsedCardsInColumn(grid, col)
        val usedInBox = getUsedCardsInBox(grid, row, col)
        
        val allUsedCards = (usedInRow + usedInColumn + usedInBox).toSet()
        
        return CardFactory.createSudokuCards().filter { card ->
            !allUsedCards.any { usedCard -> usedCard.isSameRank(card) }
        }
    }
    
    private fun getUsedCardsInRow(grid: List<List<CellState>>, row: Int): List<Card> {
        return grid[row].mapNotNull { it.card }
    }
    
    private fun getUsedCardsInColumn(grid: List<List<CellState>>, col: Int): List<Card> {
        return grid.mapNotNull { row -> row[col].card }
    }
    
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
    
    private fun hasBoxConflict(grid: List<List<CellState>>, row: Int, col: Int, card: Card): Boolean {
        val boxRow = (row / GameState.BOX_SIZE) * GameState.BOX_SIZE
        val boxCol = (col / GameState.BOX_SIZE) * GameState.BOX_SIZE
        
        for (r in boxRow until boxRow + GameState.BOX_SIZE) {
            for (c in boxCol until boxCol + GameState.BOX_SIZE) {
                if (grid[r][c].card?.rank == card.rank) {
                    return true
                }
            }
        }
        return false
    }
    
    /**
     * Toggle between different difficulty levels
     */
    fun setDifficulty(difficulty: Difficulty) {
        startNewGame(difficulty)
    }
    
    /**
     * Solve the current game (for testing purposes)
     */
    fun solveGame() {
        val currentState = _gameState.value
        val solvedGrid = SudokuLogic.generateSolvedGrid()
        
        // Create new grid with all cells filled and marked as given
        val finalGrid = solvedGrid.map { row ->
            row.map { cell -> 
                cell.copy(isGiven = true)
            }
        }
        
        _gameState.value = currentState.copy(
            grid = finalGrid,
            isGameComplete = true,
            isGameWon = true
        )
    }
}
