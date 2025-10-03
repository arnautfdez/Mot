package cat.happyband.mot.game.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.utf16CodePoint
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cat.happyband.mot.game.domain.EvaluatedLetter
import cat.happyband.mot.game.domain.LetterState
import cat.happyband.mot.utils.SoundPlayer
import io.github.vinceglb.confettikit.compose.ConfettiKit
import io.github.vinceglb.confettikit.core.Party
import io.github.vinceglb.confettikit.core.emitter.Emitter
import kotlin.time.Duration.Companion.seconds

@Composable
fun GameScreen(username: String, viewModel: GameViewModel) {
    val uiState = viewModel.uiState

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val soundPlayer = remember { SoundPlayer }

    LaunchedEffect(uiState.gameState) {
        when (uiState.gameState) {
            GameState.WON -> soundPlayer.playVictorySound()
            GameState.LOST -> soundPlayer.playLossSound()
            GameState.PLAYING -> { }
        }
    }

    if (uiState.gameState == GameState.WON) {
        ConfettiKit(
            modifier = Modifier.fillMaxSize(),
            parties = listOf(
                Party(emitter = Emitter(duration = 5.seconds).perSecond(30))
            )
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {

        if (uiState.showEndGameDialog) {
            GameEndDialog(
                uiState = uiState,
                onClose = viewModel::hideEndGameDialog,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .focusRequester(focusRequester)
                .focusable()
                .onKeyEvent { event ->
                    if (event.type == KeyEventType.KeyDown) {
                        when (event.key) {
                            Key.Enter -> {
                                viewModel.onSubmitClick()
                                true
                            }

                            Key.Backspace -> {
                                viewModel.onDeleteClick()
                                true
                            }

                            else -> {
                                val char = event.utf16CodePoint.toChar().uppercaseChar()
                                if (char in 'A'..'Z') {
                                    viewModel.onLetterClick(char)
                                    true
                                } else {
                                    false
                                }
                            }
                        }
                    } else {
                        false
                    }
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Sort, $username!", style = MaterialTheme.typography.headlineSmall)

            GameGrid(uiState)

            VirtualKeyboard(
                keyboardState = uiState.keyboardLetterStates,
                onLetterClick = { viewModel.onLetterClick(it) },
                onDeleteClick = { viewModel.onDeleteClick() },
                onSubmitClick = { viewModel.onSubmitClick() }
            )
        }
    }
}

@Composable
private fun GameGrid(uiState: GameUiState) {
    val maxGuesses = 6
    val wordLength = uiState.solution.length.takeIf { it > 0 } ?: 5

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        uiState.guesses.forEach { guess ->
            GuessRow(letters = guess)
        }

        if (uiState.guesses.size < maxGuesses) {
            CurrentGuessRow(text = uiState.currentGuess, wordLength = wordLength)
        }

        repeat(maxGuesses - uiState.guesses.size - 1) {
            if (it >= 0) {
                GuessRow(letters = List(wordLength) { EvaluatedLetter(' ', LetterState.PENDING) })
            }
        }
    }
}

@Composable
private fun GuessRow(letters: List<EvaluatedLetter>) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        letters.forEach { letter ->
            LetterBox(letter)
        }
    }
}

@Composable
private fun CurrentGuessRow(text: String, wordLength: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(wordLength) { index ->
            val char = text.getOrNull(index) ?: ' '
            LetterBox(EvaluatedLetter(char, LetterState.PENDING))
        }
    }
}

@Composable
private fun LetterBox(letter: EvaluatedLetter) {
    val backgroundColor = when (letter.state) {
        LetterState.CORRECT -> Color(0xFF6AAA64)
        LetterState.PRESENT -> Color(0xFFC9B458)
        LetterState.ABSENT -> MaterialTheme.colorScheme.secondary
        LetterState.PENDING -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .size(60.dp)
            .border(2.dp, MaterialTheme.colorScheme.secondary)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = letter.char.toString(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = if (letter.state == LetterState.PENDING) MaterialTheme.colorScheme.onBackground else Color.White
        )
    }
}

@Composable
private fun VirtualKeyboard(
    keyboardState: Map<Char, LetterState>,
    onLetterClick: (Char) -> Unit,
    onDeleteClick: () -> Unit,
    onSubmitClick: () -> Unit
) {
    val rows = listOf(
        "QWERTYUIOP",
        "ASDFGHJKL",
        "ZXCVBNM"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                row.forEach { char ->
                    val letterState = keyboardState[char] ?: LetterState.PENDING
                    KeyButton(
                        text = char.toString(),
                        state = letterState,
                        modifier = Modifier.weight(1f),
                    ) {
                        onLetterClick(char)
                    }
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            KeyButton(text = "ENTER", modifier = Modifier.weight(2f), onClick = onSubmitClick)
            KeyButton(text = "⌫", modifier = Modifier.weight(1f), onClick = onDeleteClick)
        }
    }
}

@Composable
private fun KeyButton(
    text: String,
    state: LetterState = LetterState.PENDING,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val backgroundColor = when (state) {
        LetterState.CORRECT -> Color(0xFF6AAA64)
        LetterState.PRESENT -> Color(0xFFC9B458)
        LetterState.ABSENT -> Color.DarkGray
        LetterState.PENDING -> Color.LightGray
    }
    Surface(
        modifier = modifier
            .height(50.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        color = backgroundColor
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}
