package cat.happyband.mot.game.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cat.happyband.mot.game.domain.EvaluatedLetter
import cat.happyband.mot.game.domain.LetterState
import cat.happyband.mot.game.domain.evaluateGuess
import cat.happyband.mot.game.domain.getDailyWord

enum class GameState { PLAYING, WON, LOST }

class GameViewModel {
    var uiState by mutableStateOf(GameUiState())
        private set

    init {
        newGame()
    }

    fun newGame() {
        uiState = GameUiState(solution = getDailyWord())
    }

    fun onLetterClick(lletra: Char) {
        if (uiState.gameState == GameState.PLAYING && uiState.currentGuess.length < uiState.solution.length) {
            uiState = uiState.copy(currentGuess = uiState.currentGuess + lletra)
        }
    }

    fun onDeleteClick() {
        if (uiState.currentGuess.isNotEmpty()) {
            uiState = uiState.copy(currentGuess = uiState.currentGuess.dropLast(1))
        }
    }

    fun onSubmitClick() {
        if (uiState.currentGuess.length != uiState.solution.length || uiState.gameState != GameState.PLAYING) {
            return
        }

        val result = evaluateGuess(uiState.currentGuess, uiState.solution)
        val newGuesses = uiState.guesses + listOf(result)
        val newKeyboardState = updateKeyboardState(newGuesses)

        val hasWon = result.all { it.state == LetterState.CORRECT }
        val hasLost = newGuesses.size == 6 && !hasWon

        val newGameState = when {
            hasWon -> GameState.WON
            hasLost -> GameState.LOST
            else -> GameState.PLAYING
        }

        uiState = uiState.copy(
            guesses = newGuesses,
            currentGuess = "",
            gameState = newGameState,
            keyboardLetterStates = newKeyboardState,
            showEndGameDialog = newGameState == GameState.WON || newGameState == GameState.LOST,
        )
    }

    fun hideEndGameDialog() {
        uiState = uiState.copy(showEndGameDialog = false)
    }

    private fun updateKeyboardState(guesses: List<List<EvaluatedLetter>>): Map<Char, LetterState> {
        val newStates = mutableMapOf<Char, LetterState>()

        guesses.forEach { guess ->
            guess.forEach { evaluatedLetter ->
                val char = evaluatedLetter.char
                val currentBestPriority = newStates[char]?.priority ?: -1
                val newPriority = evaluatedLetter.state.priority

                if (newPriority > currentBestPriority) {
                    newStates[char] = evaluatedLetter.state
                }
            }
        }
        return newStates
    }
}

data class GameUiState(
    val solution: String = "",
    val guesses: List<List<EvaluatedLetter>> = emptyList(),
    val currentGuess: String = "",
    val gameState: GameState = GameState.PLAYING,
    val keyboardLetterStates: Map<Char, LetterState> = emptyMap(),
    val showEndGameDialog: Boolean = false
)