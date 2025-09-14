package com.pokersudoku.game.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pokersudoku.game.R
import com.pokersudoku.game.model.Difficulty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DifficultySelector(
    selectedDifficulty: Difficulty,
    onDifficultyChanged: (Difficulty) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Difficulty:",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedDifficulty.name.lowercase().replaceFirstChar { it.uppercase() },
                    onValueChange = { },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    Difficulty.values().forEach { difficulty ->
                        DropdownMenuItem(
                            text = { 
                                Text(
                                    text = when (difficulty) {
                                        Difficulty.EASY -> stringResource(R.string.easy)
                                        Difficulty.MEDIUM -> stringResource(R.string.medium)
                                        Difficulty.HARD -> stringResource(R.string.hard)
                                        Difficulty.EXPERT -> stringResource(R.string.expert)
                                    }
                                )
                            },
                            onClick = {
                                onDifficultyChanged(difficulty)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
