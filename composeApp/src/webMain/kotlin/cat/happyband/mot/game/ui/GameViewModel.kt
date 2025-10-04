package cat.happyband.mot.game.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cat.happyband.mot.game.data.GameRepository
import cat.happyband.mot.game.domain.EvaluatedLetter
import cat.happyband.mot.game.domain.GameResult
import cat.happyband.mot.game.domain.LetterState
import cat.happyband.mot.game.domain.evaluateGuess
import cat.happyband.mot.game.domain.getDailyWord
import cat.happyband.mot.game.domain.loadWordList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

enum class GameState { PLAYING, WON, LOST }

class GameViewModel(private val currentUsername: String) {
    var uiState by mutableStateOf(GameUiState())
        private set

    private val isTimeBonusEnabled = true
    private var startTime: Long = 0

    private val repository = GameRepository()
    private val viewModelScope = CoroutineScope(Dispatchers.Default)

    private var loadedWordList: List<String> = emptyList()

    init {
        startTime = Clock.System.now().toEpochMilliseconds()
        loadDictionaryAndInitGame()
    }

    fun loadDictionaryAndInitGame() {
        viewModelScope.launch {
            loadedWordList = loadWordList()

            val word = getDailyWord(loadedWordList)
            uiState = uiState.copy(solution = word)

            loadInitialGameState()
        }
    }

    fun loadInitialGameState() {
        viewModelScope.launch {

            val latestResult = repository.getLatestResultForUser(currentUsername)

            if (latestResult != null) {
                // L'usuari ha jugat avui. Carreguem l'estat FINAL.

                // NOTA: Cal la funció de reconstrucció de tauler (reconstructGuesses)
                val finalGuesses = reconstructGuesses(latestResult)
                val finalKeyboardState = updateKeyboardState(finalGuesses)

                uiState = uiState.copy(
                    // IMPORTANT: La paraula ha de ser la mateixa del resultat, si la vas guardar!
                    // Si no vas guardar la paraula al GameResult, la deixem amb la del dia.
                    // solution = latestResult.word,
                    guesses = finalGuesses,
                    keyboardLetterStates = finalKeyboardState,
                    gameState = if (latestResult.solved) GameState.WON else GameState.LOST,
                    finalScore = latestResult.score,
                    timeSpentSeconds = latestResult.timeSpentSeconds,
                    celebrationComplete = true // Ja va celebrar, no cal repetir
                )
            } else {
                uiState = uiState.copy(gameState = GameState.PLAYING)
            }
            // Si no ha jugat avui, el joc es manté en estat PLAYING per defecte (newGame)

            uiState = uiState.copy(isLoadingGame = false)
        }
    }

    private fun reconstructGuesses(result: GameResult): List<List<EvaluatedLetter>> {
        // Per evitar errors de compilació, retornarem l'estat final amb el nombre d'intents correctes
        val solution = uiState.solution
        val numAttempts = result.attempts

        return List(numAttempts) { index ->
            // Simulem que només l'última fila es completa amb l'estat final (VERD o ABSENT/PRESENT)
            if (index == numAttempts - 1) {
                evaluateGuess(solution, solution)
            } else {
                List(solution.length) { EvaluatedLetter(' ', LetterState.PENDING) }
            }
        }
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
        println("DEBUG: Entering onSubmitClick")
        if (uiState.currentGuess.length != uiState.solution.length || uiState.gameState != GameState.PLAYING) {
            return
        }

        if (!loadedWordList.contains(uiState.currentGuess)) {
            // NOTA: Pots afegir un error a la UI, per exemple: uiState.copy(error = "Paraula no vàlida")
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

        if (newGameState == GameState.WON || newGameState == GameState.LOST) {
            println("DEBUG: Entering save block for user ${currentUsername}")
            val gameResult = GameResult(
                username = currentUsername,
                solved = hasWon,
                attempts = newGuesses.size,
                score = finalScore,
                timeSpentSeconds = timeSpentSeconds
            )

            viewModelScope.launch {
                repository.saveGameResult(gameResult)
                println("INFO: Game result for $currentUsername saved to Supabase.")
            }
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

    fun markCelebrationComplete() {
        uiState = uiState.copy(celebrationComplete = true)
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
    val celebrationComplete: Boolean = false,
    val isLoadingGame: Boolean = true,
)