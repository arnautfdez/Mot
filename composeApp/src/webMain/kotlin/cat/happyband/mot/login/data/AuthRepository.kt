package cat.happyband.mot.login.data

import cat.happyband.mot.data.SUPABASE_ANON_KEY
import cat.happyband.mot.data.SUPABASE_URL
import cat.happyband.mot.data.httpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.HttpHeaders

class AuthRepository {

    // Funció que comprova si l'usuari existeix i retorna el seu hash de contrasenya
    suspend fun getUserPassword(username: String): String? {
        try {
            // Demanem només el camp password_hash
            val response = httpClient.get("$SUPABASE_URL/rest/v1/mot_users") {
                header(HttpHeaders.Authorization, "Bearer $SUPABASE_ANON_KEY")
                header("apikey", SUPABASE_ANON_KEY)

                // CRUCIAL: Filtra per l'usuari Específic
                parameter("username", "eq.$username")
                // Selecciona NOMÉS la columna de la contrasenya
                parameter("select", "password")
            }

            // La resposta serà una llista amb un mapa com: [{"password_hash": "1234"}]
            val result = response.body<List<Map<String, String>>>().firstOrNull()

            // Si trobem un resultat, retornem el valor del hash
            return result?.get("password")

        } catch (e: Exception) {
            println("ERROR retrieving password: ${e.message}")
            return null
        }
    }
}