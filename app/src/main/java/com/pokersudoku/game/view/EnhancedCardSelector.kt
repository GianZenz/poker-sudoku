package com.pokersudoku.game.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pokersudoku.game.model.Card
import com.pokersudoku.game.model.CellState
import com.pokersudoku.game.ui.theme.*

@Composable
fun EnhancedCardSelector(
    availableCards: List<Card>,
    selectedCard: Card?,
    onCardSelected: (Card) -> Unit,
    selectedCell: Pair<Int, Int>?,
    onPlaceCard: (Int, Int, Card) -> Unit,
    grid: List<List<CellState>>, // Added to show available cards for position
    getAvailableCardsForPosition: (Int, Int) -> List<Card> // Function to get valid cards for position
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Select a card to place:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Show available cards for selected position
            selectedCell?.let { (row, col) ->
                if (!grid[row][col].isGiven) {
                    val validCards = getAvailableCardsForPosition(row, col)
                    
                    if (validCards.isNotEmpty()) {
                        Text(
                            text = "Valid cards for selected position:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(validCards) { card ->
                                EnhancedSelectableCard(
                                    card = card,
                                    isSelected = selectedCard == card,
                                    isValid = true,
                                    onClick = { onCardSelected(card) }
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
            
            Text(
                text = "All available cards:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(availableCards) { card ->
                    val isValidForPosition = selectedCell?.let { (row, col) ->
                        if (grid[row][col].isGiven) false
                        else getAvailableCardsForPosition(row, col).contains(card)
                    } ?: true
                    
                    EnhancedSelectableCard(
                        card = card,
                        isSelected = selectedCard == card,
                        isValid = isValidForPosition,
                        onClick = { onCardSelected(card) }
                    )
                }
            }
            
            // Show placement instructions when a cell and card are selected
            if (selectedCell != null && selectedCard != null) {
                Spacer(modifier = Modifier.height(12.dp))
                
                val isValidMove = if (grid[selectedCell.first][selectedCell.second].isGiven) {
                    false
                } else {
                    getAvailableCardsForPosition(selectedCell.first, selectedCell.second).contains(selectedCard)
                }
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isValidMove) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.errorContainer
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Place ${selectedCard.getDisplayText()} at (${selectedCell.first + 1}, ${selectedCell.second + 1})",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isValidMove) {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onErrorContainer
                                }
                            )
                            
                            if (!isValidMove) {
                                Text(
                                    text = "This move is not valid for the selected position",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                        
                        Button(
                            onClick = { onPlaceCard(selectedCell.first, selectedCell.second, selectedCard) },
                            enabled = isValidMove,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isValidMove) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.error
                                }
                            )
                        ) {
                            Text("Place")
                        }
                    }
                }
            }
            
            // Show hint card if available
            selectedCell?.let { cell ->
                if (selectedCard == null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap a card above to place it in the selected cell",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun EnhancedSelectableCard(
    card: Card,
    isSelected: Boolean,
    isValid: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardColor = if (card.suit.isRed) {
        CardRed
    } else {
        CardBlack
    }
    
    val borderColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isValid -> CardBorder
        else -> MaterialTheme.colorScheme.error
    }
    
    val borderWidth = if (isSelected) 3.dp else 1.dp
    val alpha = if (isValid) 1f else 0.5f
    
    Box(
        modifier = modifier
            .width(60.dp)
            .height(80.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(CardBackground.copy(alpha = alpha))
            .border(borderWidth, borderColor, RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = card.rank.symbol,
                color = cardColor.copy(alpha = alpha),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = card.suit.symbol,
                color = cardColor.copy(alpha = alpha),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}