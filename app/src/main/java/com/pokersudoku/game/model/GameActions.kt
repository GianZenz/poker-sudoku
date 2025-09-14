package com.pokersudoku.game.model

/**
 * Sealed class representing different types of game actions
 */
sealed class GameAction {
    data class PlaceCard(val row: Int, val col: Int, val card: Card) : GameAction()
    data class RemoveCard(val row: Int, val col: Int) : GameAction()
    data class SelectCell(val row: Int, val col: Int) : GameAction()
    object ClearSelection : GameAction()
    data class SelectCard(val card: Card) : GameAction()
    object ClearCardSelection : GameAction()
    data class StartNewGame(val difficulty: Difficulty) : GameAction()
    object GetHint : GameAction()
    data class UpdateTimer(val seconds: Long) : GameAction()
}

/**
 * Immutable game state updater to prevent concurrency issues
 */
object GameStateUpdater {
    
    fun updateCellState(
        grid: List<List<CellState>>,
        row: Int,
        col: Int,
        updater: (CellState) -> CellState
    ): List<List<CellState>> {
        return grid.mapIndexed { r, rowList ->
            if (r == row) {
                rowList.mapIndexed { c, cell ->
                    if (c == col) updater(cell) else cell
                }
            } else {
                rowList
            }
        }
    }
    
    fun updateAllCells(
        grid: List<List<CellState>>,
        updater: (CellState, Int, Int) -> CellState
    ): List<List<CellState>> {
        return grid.mapIndexed { r, rowList ->
            rowList.mapIndexed { c, cell ->
                updater(cell, r, c)
            }
        }
    }
    
    fun clearSelection(grid: List<List<CellState>>): List<List<CellState>> {
        return updateAllCells(grid) { cell, _, _ ->
            cell.copy(isSelected = false)
        }
    }
    
    fun selectCell(grid: List<List<CellState>>, targetRow: Int, targetCol: Int): List<List<CellState>> {
        return updateAllCells(grid) { cell, row, col ->
            cell.copy(isSelected = row == targetRow && col == targetCol)
        }
    }
}