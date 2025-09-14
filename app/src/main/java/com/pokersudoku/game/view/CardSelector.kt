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
import com.pokersudoku.game.model.Suit
import com.pokersudoku.game.ui.theme.*

@Composable
fun CardSelector(
    availableCards: List<Card>,
    selectedCard: Card?,
    onCardSelected: (Card) -> Unit,
    selectedCell: Pair<Int, Int>?,
    onPlaceCard: (Int, Int, Card) -> Unit
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
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(availableCards) { card ->
                    SelectableCard(
                        card = card,
                        isSelected = selectedCard == card,
                        onClick = { onCardSelected(card) }
                    )
                }
            }
            
            // Show placement instructions when a cell and card are selected
            if (selectedCell != null && selectedCard != null) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Place ${selectedCard.getDisplayText()} at (${selectedCell.first + 1}, ${selectedCell.second + 1})",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        
                        Button(
                            onClick = { onPlaceCard(selectedCell.first, selectedCell.second, selectedCard) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Place")
                        }
                    }
                }
            }
            
            // Show hint card if available
            selectedCell?.let { cell ->
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

@Composable
fun SelectableCard(
    card: Card,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardColor = if (card.suit == Suit.HEARTS || card.suit == Suit.DIAMONDS) {
        CardRed
    } else {
        CardBlack
    }
    
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else CardBorder
    val borderWidth = if (isSelected) 3.dp else 1.dp
    
    Box(
        modifier = modifier
            .width(60.dp)
            .height(80.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(CardBackground)
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
                
                val cardColor = if (card.suit == Suit.HEARTS || card.suit == Suit.DIAMONDS) {
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
