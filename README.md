# Poker Sudoku

A unique twist on the classic Sudoku puzzle game that uses playing cards instead of numbers!

## Game Concept

Instead of using numbers 1-9, this Sudoku game uses playing cards with ranks Ace through 9. The traditional Sudoku rules apply:
- Each row must contain one of each rank (Ace, 2, 3, 4, 5, 6, 7, 8, 9)
- Each column must contain one of each rank
- Each 3x3 box must contain one of each rank
- Cards of the same rank but different suits are considered the same for Sudoku rules

## Features

### Core Gameplay
- **9x9 Sudoku grid** with poker card ranks (Ace through 9)
- **Three difficulty levels**: Easy, Medium, Hard
- **Real-time conflict detection** - highlights conflicting cards in red
- **Card selection system** - tap cards to place them on the grid
- **Hint system** - get suggestions for difficult positions

### Game Controls
- **New Game** - Start a fresh puzzle with your chosen difficulty
- **Check Solution** - Verify if your current solution is correct
- **Hint** - Get a suggested card for the selected position
- **Timer** - Track how long you've been playing
- **Move Counter** - See how many moves you've made

### Visual Design
- **Beautiful card graphics** with proper suit colors (red for hearts/diamonds, black for clubs/spades)
- **Clear grid layout** with 3x3 box borders
- **Intuitive card selector** showing all available cards
- **Modern Material Design** UI with smooth animations

## How to Play

1. **Start a New Game**: Choose your difficulty level and tap "New Game"
2. **Select a Cell**: Tap on any empty cell in the grid to select it
3. **Choose a Card**: Tap a card from the bottom selector
4. **Place the Card**: Tap "Place" or the card will be placed automatically
5. **Complete the Puzzle**: Fill all cells following Sudoku rules
6. **Win**: Complete the puzzle without conflicts to win!

## Technical Details

### Architecture
- **MVVM Pattern** with ViewModels managing game state
- **Jetpack Compose** for modern, declarative UI
- **Kotlin Coroutines** for asynchronous operations
- **Material Design 3** for beautiful, consistent UI

### Game Logic
- **Backtracking algorithm** for puzzle generation
- **Conflict detection** for real-time validation
- **Hint system** with smart card suggestions
- **Timer and statistics** tracking

### Project Structure
```
app/src/main/java/com/pokersudoku/game/
├── model/           # Game models (Card, GameState, etc.)
├── logic/           # Sudoku game logic
├── viewmodel/       # ViewModels for state management
├── view/            # Compose UI components
└── ui/theme/        # App theming and colors
```

## Building the Project

### Prerequisites
- Android Studio Hedgehog or later
- Android SDK 24+ (Android 7.0)
- Kotlin 1.9.10+

### Build Steps
1. Clone or download the project
2. Open in Android Studio
3. Sync Gradle files
4. Build and run on device/emulator

### Dependencies
- **AndroidX Core**: Core Android functionality
- **Jetpack Compose**: Modern UI toolkit
- **Material Design 3**: Beautiful UI components
- **Lifecycle ViewModel**: State management
- **Kotlin Coroutines**: Asynchronous programming

## Game Rules

### Traditional Sudoku Rules
- Fill the 9×9 grid with cards so that each row, column, and 3×3 box contains exactly one of each rank
- Use cards with ranks: Ace (1), 2, 3, 4, 5, 6, 7, 8, 9
- Suits don't matter for Sudoku rules - only ranks count

### Poker Sudoku Specifics
- **36 cards total**: One of each rank (Ace-9) in each suit (♠♣♥♦)
- **Same rank, different suits**: Considered equivalent for Sudoku purposes
- **Visual distinction**: Red cards (♥♦) and black cards (♠♣) for easy identification
- **Conflict highlighting**: Conflicting cards are highlighted in red

## Future Enhancements

- **Multiple themes** (different card designs)
- **Achievement system** (complete puzzles, time challenges)
- **Statistics tracking** (best times, completion rates)
- **Undo/Redo functionality**
- **Sound effects and animations**
- **Multiplayer modes**
- **Custom puzzle creation**

## Contributing

Feel free to contribute to this project by:
- Reporting bugs
- Suggesting new features
- Submitting pull requests
- Improving documentation

## License

This project is open source and available under the MIT License.

---

**Enjoy playing Poker Sudoku!** 🃏🎯
