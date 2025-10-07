package cat.happyband.mot.game.domain

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import mot.composeapp.generated.resources.Res

suspend fun loadWordList(): List<String> {
    val rawTextBytes = Res.readBytes("files/PARAULES_5_SENSE_ACCENTS.txt")
    val text = rawTextBytes.decodeToString()
    return text.split("\n")
}

fun getDailyWord(wordList: List<String>): String {
    if (wordList.isEmpty()) return "ERROR"

    val now = Clock.System.now()
    val dayOfYear = now.toLocalDateTime(TimeZone.currentSystemDefault()).dayOfYear

    val index = dayOfYear % wordList.size

    return wordList[index]
}

enum class LetterState(val priority: Int) {
    // La prioritat més alta té el número més alt
    CORRECT(priority = 3),  // Green - Highest Priority
    PRESENT(priority = 2),  // Yellow - Second Highest
    ABSENT(priority = 1),   // Gray - Third Highest
    PENDING(priority = 0)   // Default/Unused - Lowest Priority
}

// Aquesta data class representarà cada lletra que es mostra a la graella
data class EvaluatedLetter(val char: Char, val state: LetterState)

// La funció principal que comprova un intent contra la solució
fun evaluateGuess(intent: String, paraulaCorrecta: String): List<EvaluatedLetter> {
    val resultat =
        MutableList(paraulaCorrecta.length) { EvaluatedLetter(intent[it], LetterState.ABSENT) }
    val lletresCorrectesComptador = paraulaCorrecta.groupingBy { it }.eachCount().toMutableMap()

    // 1a passada: Marcar les lletres correctes (verdes)
    intent.forEachIndexed { index, char ->
        if (char == paraulaCorrecta[index]) {
            resultat[index] = resultat[index].copy(state = LetterState.CORRECT)
            lletresCorrectesComptador[char] = lletresCorrectesComptador.getValue(char) - 1
        }
    }

    // 2a passada: Marcar les lletres presents (grogues)
    intent.forEachIndexed { index, char ->
        if (resultat[index].state != LetterState.CORRECT && (lletresCorrectesComptador[char]
                ?: 0) > 0
        ) {
            resultat[index] = resultat[index].copy(state = LetterState.PRESENT)
            lletresCorrectesComptador[char] = lletresCorrectesComptador.getValue(char) - 1
        }
    }

    return resultat
}