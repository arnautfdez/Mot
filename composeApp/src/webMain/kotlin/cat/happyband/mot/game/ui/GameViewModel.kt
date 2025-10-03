package cat.happyband.mot.game.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cat.happyband.mot.game.domain.EvaluatedLetter
import cat.happyband.mot.game.domain.LetterState
import cat.happyband.mot.game.domain.evaluateGuess
import cat.happyband.mot.game.domain.getDailyWord
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

enum class GameState { PLAYING, WON, LOST }

@OptIn(ExperimentalTime::class)
class GameViewModel {
    var uiState by mutableStateOf(GameUiState())
        private set

    private val isTimeBonusEnabled = true
    private var startTime: Long = 0

    init {
        startTime = Clock.System.now().toEpochMilliseconds()
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

        val finalScore: Int
        val timeSpentSeconds: Long

        if (hasWon) {
            val endTime = Clock.System.now().toEpochMilliseconds()
            val durationMs = endTime - startTime
            timeSpentSeconds = durationMs / 1000

            // 1. PUNTS PER INTENTS (Base 700 punts, recompensa l'eficiència)
            val attemptsUsed = newGuesses.size
            val scoreAttempts = (7 - attemptsUsed) * 100

            // 2. BONUS PER VELOCITAT (Màxim 300 punts, Mínim 0)
            val scoreTimeBonus = if (isTimeBonusEnabled) {
                // Formula: max(0, 360 - Temps en Segons)
                val potentialTimeScore = 360 - timeSpentSeconds.toInt()
                // Assegurem que el bonus mai supera 300 punts i mai és negatiu
                minOf(300, maxOf(0, potentialTimeScore))
            } else {
                0
            }

            // 3. PUNTS TOTALS
            finalScore = scoreAttempts + scoreTimeBonus

        } else {
            finalScore = 0
            timeSpentSeconds = 0
        }

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
            finalScore = finalScore,
            timeSpentSeconds = timeSpentSeconds
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
    val showEndGameDialog: Boolean = false,
    val finalScore: Int = 0,
    val timeSpentSeconds: Long = 0,
)