package cat.happyband.mot.game.data

import cat.happyband.mot.data.SUPABASE_ANON_KEY
import cat.happyband.mot.data.SUPABASE_URL
import cat.happyband.mot.data.TABLE_NAME
import cat.happyband.mot.game.domain.GameResult
import cat.happyband.mot.data.httpClient
import cat.happyband.mot.utils.getStartOfDayISO
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.datetime.Clock

class GameRepository {

    suspend fun saveGameResult(result: GameResult) {

        try {
            httpClient.post("$SUPABASE_URL/rest/v1/$TABLE_NAME") {
                header(HttpHeaders.Authorization, "Bearer $SUPABASE_ANON_KEY")
                header("apikey", SUPABASE_ANON_KEY)
                contentType(ContentType.Application.Json)
                setBody(result)
            }
        } catch (e: Exception) {
            println("ERROR saving game result to Supabase: ${e.message}")
        }
    }

    suspend fun fetchAllGameResults(): List<GameResult> {
        try {
            return httpClient.get("$SUPABASE_URL/rest/v1/$TABLE_NAME") {
                header(HttpHeaders.Authorization, "Bearer $SUPABASE_ANON_KEY")
                header("apikey", SUPABASE_ANON_KEY)
            }.body<List<GameResult>>()
        } catch (e: Exception) {
            println("ERROR fetching all results from Supabase: ${e.message}")
            return emptyList()
        }
    }


    suspend fun getLatestResultForUser(username: String): GameResult? {
        // La URL es la teva taula de resultats
        val url = "$SUPABASE_URL/rest/v1/$TABLE_NAME"

        // Per a la prova, utilitzarem una data fictícia, però el principi és aquest:
        val startOfToday = getStartOfDayISO()

        try {
            val response = httpClient.get(url) {
                header(HttpHeaders.Authorization, "Bearer $SUPABASE_ANON_KEY")
                header("apikey", SUPABASE_ANON_KEY)

                // FILTRES CLAU: Usuari i Data
                parameter("username", "eq.$username")
                // CRUCIAL: Filtra per registres creats avui (greater than or equal)
                parameter("created_at", "gte.$startOfToday")

                parameter("order", "created_at.desc") // Obtenir el més recent (l'únic que hi hauria)
                parameter("limit", 1) // Només ens cal la primera fila
            }

            val results = response.body<List<GameResult>>()

            return results.firstOrNull() // Retorna el resultat si existeix, si no, null
        } catch (e: Exception) {
            println("ERROR getting latest result: ${e.message}")
            return null
        }
    }
}