package com.pokersudoku.game.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.pokersudoku.game.model.Difficulty
import com.pokersudoku.game.view.GameScreen
import com.pokersudoku.game.view.MainMenuScreen
import com.pokersudoku.game.viewmodel.GameViewModel

@Composable
fun AppNavigation(
    gameViewModel: GameViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.MainMenu) }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (currentScreen) {
            AppScreen.MainMenu -> {
                MainMenuScreen(
                    onStartGame = { difficulty ->
                        gameViewModel.startNewGame(difficulty)
                        currentScreen = AppScreen.Game
                    }
                )
            }
            AppScreen.Game -> {
                GameScreen(
                    viewModel = gameViewModel,
                    onBackToMenu = {
                        currentScreen = AppScreen.MainMenu
                    }
                )
            }
        }
    }
}

sealed class AppScreen {
    object MainMenu : AppScreen()
    object Game : AppScreen()
}
