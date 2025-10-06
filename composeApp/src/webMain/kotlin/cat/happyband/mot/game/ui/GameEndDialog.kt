package cat.happyband.mot.game.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cat.happyband.mot.utils.shareResult

@Composable
fun GameEndDialog(
    uiState: GameUiState,
    onClose: () -> Unit,
    onGenerateShareText: () -> String,
) {
    val isWon = uiState.gameState == GameState.WON

    AlertDialog(
        onDismissRequest = onClose,

        title = {
            Text(text = if (isWon) "VICTÒRIA!" else "PARTIDA ACABADA")
        },

        text = {
            Column {
                if (isWon) {
                    Text("Ho has aconseguit! La teva partida ha estat guardada.")
                    Text("Has trigat ${uiState.timeSpentSeconds} segons.")
                    Text("Puntuació: ${uiState.finalScore}")
                } else {
                    Text("Has esgotat els 6 intents.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("La paraula era: ${uiState.solution}")
                }
            }
        },
        confirmButton = {
            Button(onClick = onClose) {
                Text("TANCAR")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    val textToShare = onGenerateShareText()
                    shareResult(textToShare)
                }
            ) {
                Text("Compartir Resultat (WhatsApp)")
            }
        }
        // ----------------------------------------------------
    )
}