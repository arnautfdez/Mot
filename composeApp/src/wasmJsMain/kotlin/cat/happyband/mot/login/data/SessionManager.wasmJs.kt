package cat.happyband.mot.login.data

import org.w3c.dom.get
import org.w3c.dom.set
import kotlinx.browser.window

private const val USER_KEY = "mot_current_user"
private const val CURRENT_GAME_STATE_KEY = "mot_current_game_state"

actual fun getSessionManager(): SessionManager = LocalStorageSessionManager()

class LocalStorageSessionManager : SessionManager {

    override fun saveSession(username: String) {
        window.localStorage[USER_KEY] = username
    }

    override fun getSession(): String? {
        return window.localStorage[USER_KEY]
    }

    override fun clearSession() {
        window.localStorage.removeItem(USER_KEY)
    }

    override fun saveCurrentGameState(json: String) {
        window.localStorage[CURRENT_GAME_STATE_KEY] = json
    }

    override fun getCurrentGameState(): String? {
        return window.localStorage[CURRENT_GAME_STATE_KEY]
    }
}