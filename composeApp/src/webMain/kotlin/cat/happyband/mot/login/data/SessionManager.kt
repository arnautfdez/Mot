package cat.happyband.mot.login.data

interface SessionManager {
    fun saveSession(username: String)
    fun getSession(): String?
    fun clearSession()

    fun saveCurrentGameState(json: String)

    fun getCurrentGameState(): String?
}

expect fun getSessionManager(): SessionManager