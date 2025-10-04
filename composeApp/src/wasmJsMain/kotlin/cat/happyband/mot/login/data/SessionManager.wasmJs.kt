package cat.happyband.mot.login.data

import org.w3c.dom.get
import org.w3c.dom.set
import kotlinx.browser.window

private const val USER_KEY = "mot_current_user"

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
}