package com.pokersudoku.game.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pokersudoku.game.model.CellState
import com.pokersudoku.game.model.GameState
import com.pokersudoku.game.ui.theme.*

@Composable
fun GameGrid(
    grid: List<List<CellState>>,
    selectedCell: Pair<Int, Int>?,
    onCellClick: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(GameState.GRID_SIZE),
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(1.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            itemsIndexed(grid.flatten()) { index, cell ->
                val row = index / GameState.GRID_SIZE
                val col = index % GameState.GRID_SIZE
                
                SudokuCell(
                    cell = cell,
                    isSelected = selectedCell == Pair(row, col),
                    isBoxBorder = isBoxBorder(row, col),
                    onClick = { onCellClick(row, col) },
                    modifier = Modifier
                        .aspectRatio(1f)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun SudokuCell(
    cell: CellState,
    isSelected: Boolean,
    isBoxBorder: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        cell.hasConflict -> ConflictCell
        isSelected -> SelectedCell
        cell.isGiven -> MaterialTheme.colorScheme.surface
        else -> MaterialTheme.colorScheme.background
    }
    
    val borderWidth = if (isBoxBorder) 2.dp else 1.dp
    val borderColor = if (isBoxBorder) MaterialTheme.colorScheme.onSurface else CardBorder
    
    Box(
        modifier = modifier
            .background(backgroundColor)
            .border(borderWidth, borderColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        cell.card?.let { card ->
            CardDisplay(
                card = card,
                isGiven = cell.isGiven,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun CardDisplay(
    card: com.pokersudoku.game.model.Card,
    isGiven: Boolean,
    modifier: Modifier = Modifier
) {
    val cardColor = if (card.suit == com.pokersudoku.game.model.Suit.HEARTS || 
                       card.suit == com.pokersudoku.game.model.Suit.DIAMONDS) {
        CardRed
    } else {
        CardBlack
    }
    
    val alpha = if (isGiven) 1f else 0.8f
    
    Box(
        modifier = modifier
            .background(
                color = CardBackground.copy(alpha = alpha),
                shape = MaterialTheme.shapes.small
            )
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = card.rank.symbol,
                color = cardColor.copy(alpha = alpha),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            Text(
                text = card.suit.symbol,
                color = cardColor.copy(alpha = alpha),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

private fun isBoxBorder(row: Int, col: Int): Boolean {
    return row % GameState.BOX_SIZE == 0 || col % GameState.BOX_SIZE == 0
}
