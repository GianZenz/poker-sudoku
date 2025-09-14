package com.pokersudoku.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.pokersudoku.game.navigation.AppNavigation
import com.pokersudoku.game.ui.theme.PokerSudokuTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokerSudokuTheme {
                AppNavigation()
            }
        }
    }
}
