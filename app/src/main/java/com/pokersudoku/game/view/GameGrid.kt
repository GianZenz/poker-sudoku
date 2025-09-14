package com.pokersudoku.game.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.sp
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
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f), // Force square aspect ratio
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // subtle shadow
        shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 8.dp, bottomEnd = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(2.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Create 3 box rows
                repeat(3) { boxRow ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        // Create 3 box columns
                        repeat(3) { boxCol ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .then(
                                        if (boxCol < 2) {
                                            Modifier.border(
                                                width = 1.dp,
                                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)
                                            )
                                        } else Modifier
                                    )
                                    .then(
                                        if (boxRow < 2) {
                                            Modifier.border(
                                                width = 1.dp,
                                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)
                                            )
                                        } else Modifier
                                    )
                            ) {
                                // Create 3x3 cells within each box
                                Column(modifier = Modifier.fillMaxSize()) {
                                    repeat(3) { cellRow ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f)
                                        ) {
                                                                    repeat(3) { cellCol ->
                                                val actualRow = boxRow * 3 + cellRow
                                                val actualCol = boxCol * 3 + cellCol
                                                
                                                SudokuCell(
                                                    cell = grid[actualRow][actualCol],
                                                    isSelected = selectedCell == Pair(actualRow, actualCol),
                                                    row = actualRow,
                                                    col = actualCol,
                                                    onClick = { onCellClick(actualRow, actualCol) },
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .fillMaxHeight()
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SudokuCell(
    cell: CellState,
    isSelected: Boolean,
    row: Int,
    col: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        cell.hasConflict -> ConflictCell
        isSelected -> SelectedCell
        cell.isGiven -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surface
    }
    
    // Subtle borders - only 3x3 box boundaries are slightly thicker
    val borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    val thickBorderColor = MaterialTheme.colorScheme.outline
    
    // Determine border thickness
    val topBorder = if (row % GameState.BOX_SIZE == 0) 2.dp else 0.5.dp
    val leftBorder = if (col % GameState.BOX_SIZE == 0) 2.dp else 0.5.dp
    val rightBorder = if ((col + 1) % GameState.BOX_SIZE == 0 || col == GameState.GRID_SIZE - 1) 2.dp else 0.5.dp
    val bottomBorder = if ((row + 1) % GameState.BOX_SIZE == 0 || row == GameState.GRID_SIZE - 1) 2.dp else 0.5.dp
    
    Box(
        modifier = modifier
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(
                top = if (topBorder > 1.dp) 1.dp else 0.dp,
                start = if (leftBorder > 1.dp) 1.dp else 0.dp,
                end = if (rightBorder > 1.dp) 1.dp else 0.dp,
                bottom = if (bottomBorder > 1.dp) 1.dp else 0.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        // Add subtle separators instead of heavy borders
        if (col < GameState.GRID_SIZE - 1 && (col + 1) % GameState.BOX_SIZE != 0) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(0.5.dp)
                    .background(borderColor)
                    .align(Alignment.CenterEnd)
            )
        }
        
        if (row < GameState.GRID_SIZE - 1 && (row + 1) % GameState.BOX_SIZE != 0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(borderColor)
                    .align(Alignment.BottomCenter)
            )
        }
        
        cell.card?.let { card ->
            CardDisplay(
                card = card,
                isGiven = cell.isGiven
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
    val cardColor = if (card.suit.isRed) {
        CardRed
    } else {
        CardBlack
    }
    
    val fontWeight = if (isGiven) FontWeight.Bold else FontWeight.Medium
    val alpha = if (isGiven) 1f else 0.85f
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.padding(2.dp)
    ) {
        Text(
            text = card.rank.symbol,
            color = cardColor.copy(alpha = alpha),
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
            fontWeight = fontWeight
        )
        Text(
            text = card.suit.symbol,
            color = cardColor.copy(alpha = alpha),
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
            fontWeight = fontWeight
        )
    }
}
