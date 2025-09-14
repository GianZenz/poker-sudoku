package com.pokersudoku.game.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import com.pokersudoku.game.R
import com.pokersudoku.game.model.*
import com.pokersudoku.game.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onBackToMenu: (() -> Unit)? = null
) {
    val gameState by viewModel.gameState.collectAsState()
    val availableCards by viewModel.availableCards.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var titleClickCount by remember { mutableStateOf(0) }
    var showSolveButton by remember { mutableStateOf(false) }
    
    // Game timer
    LaunchedEffect(gameState.isGameComplete, gameState.difficulty) {
        try {
            // Reset timer when starting a new game
            if (!gameState.isGameComplete && gameState.timeElapsed == 0L) {
                while (!gameState.isGameComplete) {
                    kotlinx.coroutines.delay(1000)
                    viewModel.updateTimer(gameState.timeElapsed + 1)
                }
            }
        } catch (e: Exception) {
            // Handle any exceptions that might occur during timer updates
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Add padding above the header
        Spacer(modifier = Modifier.height(16.dp))
        
        // Header with game info and controls
        GameHeader(
            gameState = gameState,
            onNewGame = { viewModel.startNewGame(gameState.difficulty) },
            onCheckSolution = { 
                val result = viewModel.checkSolution()
                scope.launch {
                    snackbarHostState.showSnackbar(result)
                }
            },
            onHint = { 
                val hint = viewModel.getHint()
                scope.launch {
                    snackbarHostState.showSnackbar(hint)
                }
            },
            onBackToMenu = onBackToMenu,
            onTitleClick = {
                titleClickCount++
                if (titleClickCount >= 5) {
                    showSolveButton = !showSolveButton
                    titleClickCount = 0
                }
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Main game grid
        GameGrid(
            grid = gameState.grid,
            selectedCell = gameState.selectedCell,
            onCellClick = { row, col -> viewModel.selectCell(row, col) },
            modifier = Modifier.weight(1f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Secret solve button (appears after tapping title 5 times)
        if (showSolveButton) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔧 Developer Tools",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Button(
                        onClick = { 
                            viewModel.solveGame()
                            showSolveButton = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Solve Game")
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        

        
        // Card selector
        CardSelector(
            availableCards = availableCards,
            selectedCard = gameState.selectedCard,
            onCardSelected = { card -> viewModel.selectCard(card) },
            selectedCell = gameState.selectedCell,
            onPlaceCard = { row, col, card -> 
                viewModel.placeCard(row, col, card)?.let { errorMessage ->
                    scope.launch {
                        snackbarHostState.showSnackbar(errorMessage)
                    }
                }
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Game stats
        GameStats(gameState = gameState)
    }
    
    // Snackbar for showing messages
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier.padding(16.dp)
    )
    
    // Show game completion dialog
    if (gameState.isGameWon) {
        GameCompleteDialog(
            gameState = gameState,
            onDismiss = { /* Could restart game or show stats */ },
            onNewGame = { viewModel.startNewGame(gameState.difficulty) }
        )
    }
}

@Composable
fun GameHeader(
    gameState: GameState,
    onNewGame: () -> Unit,
    onCheckSolution: () -> Unit,
    onHint: () -> Unit,
    onBackToMenu: (() -> Unit)? = null,
    onTitleClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (onBackToMenu != null) {
                        IconButton(
                            onClick = onBackToMenu,
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text(
                                text = "←",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = "Poker Sudoku",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = if (onTitleClick != null) {
                            Modifier.clickable { onTitleClick() }
                        } else {
                            Modifier
                        }
                    )
                }
                
                IconButton(
                    onClick = onNewGame,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "🔄",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var showHowToPlay by remember { mutableStateOf(false) }
                    
                    Text(
                        text = "Difficulty: ${gameState.difficulty.name}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    IconButton(
                        onClick = { showHowToPlay = true },
                        modifier = Modifier.size(24.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Text(
                            text = "?",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    
                    if (showHowToPlay) {
                        HowToPlayDialog(onDismiss = { showHowToPlay = false })
                    }
                }
                
                Text(
                    text = "Time: ${formatTime(gameState.timeElapsed)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Horizontal button row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onCheckSolution,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.check_solution))
                }
                
                OutlinedButton(
                    onClick = onHint,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.hint))
                }
            }
        }
    }
}

@Composable
fun GameStats(gameState: GameState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                label = "Moves",
                value = gameState.moveCount.toString()
            )
            StatItem(
                label = "Hints",
                value = gameState.hintsUsed.toString()
            )
            StatItem(
                label = "Progress",
                value = "${getGridProgress(gameState.grid)}%"
            )
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun GameCompleteDialog(
    gameState: GameState,
    onDismiss: () -> Unit,
    onNewGame: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.congratulations),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.solution_correct),
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Time: ${formatTime(gameState.timeElapsed)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Moves: ${gameState.moveCount}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Hints: ${gameState.hintsUsed}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = {
            Button(onClick = onNewGame) {
                Text(stringResource(R.string.new_game))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

private fun formatTime(seconds: Long): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}

private fun getGridProgress(grid: List<List<CellState>>): Int {
    val totalCells = GameState.GRID_SIZE * GameState.GRID_SIZE
    val filledCells = grid.sumOf { row -> row.count { it.card != null } }
    return (filledCells * 100 / totalCells)
}
