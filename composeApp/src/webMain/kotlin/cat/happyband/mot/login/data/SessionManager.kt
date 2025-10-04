package cat.happyband.mot.login.data

interface SessionManager {
    fun saveSession(username: String)
    fun getSession(): String?
    fun clearSession()
}

expect fun getSessionManager(): SessionManager