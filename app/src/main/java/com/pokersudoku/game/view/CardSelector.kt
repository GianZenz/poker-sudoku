package com.pokersudoku.game.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pokersudoku.game.model.Card
import com.pokersudoku.game.model.Suit
import com.pokersudoku.game.ui.theme.*

@Composable
fun CardSelector(
    availableCards: List<Card>,
    selectedCard: Card?,
    onCardSelected: (Card) -> Unit,
    selectedCell: Pair<Int, Int>?,
    onPlaceCard: (Int, Int, Card) -> Unit,
    errorMessage: String? = null // Add error message parameter
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // subtle shadow
        shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 12.dp, bottomEnd = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Text(
                text = "Select a card to place:",
                style = MaterialTheme.typography.titleSmall, // Reduced from titleMedium
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(6.dp)) // Reduced from 8dp
            
            // Card selection row - fixed height
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp), // Reduced from 8dp
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp) // Reduced from 80dp
            ) {
                items(availableCards) { card ->
                    SelectableCard(
                        card = card,
                        isSelected = selectedCard == card,
                        onClick = { onCardSelected(card) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(6.dp)) // Reduced from 8dp
            
            // Status area - fixed height
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Takes remaining space
            ) {
                when {
                    // Show error message if present
                    errorMessage != null -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "⚠️",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(
                                    text = errorMessage,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                    
                    // Show placement instructions when a cell and card are selected
                    selectedCell != null && selectedCard != null -> {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp), // Increased padding for button area
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Place ${selectedCard.getDisplayText()} at (${selectedCell.first + 1}, ${selectedCell.second + 1})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.weight(1f)
                                )
                                
                                Button(
                                    onClick = { onPlaceCard(selectedCell.first, selectedCell.second, selectedCard) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    ),
                                    modifier = Modifier.height(40.dp), // Much larger button
                                    contentPadding = PaddingValues(horizontal = 16.dp) // More padding
                                ) {
                                    Text(
                                        text = "Place",
                                        style = MaterialTheme.typography.bodyMedium, // Larger text
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                    
                    // Show hint text when a cell is selected
                    selectedCell != null -> {
                        Text(
                            text = "Tap a card above to place it in the selected cell",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    
                    // Default instruction
                    else -> {
                        Text(
                            text = "Select a cell on the grid first",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SelectableCard(
    card: Card,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardColor = if (card.suit.isRed) {
        CardRed
    } else {
        CardBlack
    }
    
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else CardBorder
    val borderWidth = if (isSelected) 3.dp else 1.dp

    Card(
        modifier = modifier
            .width(64.dp)
            .height(84.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
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
                    color = cardColor,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = card.suit.symbol,
                    color = cardColor,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun HintCard(
    card: Card?,
    modifier: Modifier = Modifier
) {
    if (card != null) {
        Card(
            modifier = modifier,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "💡 Hint: Try ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                
                val cardColor = if (card.suit.isRed) {
                    CardRed
                } else {
                    CardBlack
                }
                
                Box(
                    modifier = Modifier
                        .background(
                            color = CardBackground,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = card.rank.symbol,
                            color = cardColor,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = card.suit.symbol,
                            color = cardColor,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
