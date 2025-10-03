package cat.happyband.mot.login.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class LoginViewModel {
    var uiState by mutableStateOf(LoginUiState())
        private set

    fun onUsernameChange(username: String) {
        uiState = uiState.copy(username = username, error = null)
    }

    fun onPasswordChange(password: String) {
        uiState = uiState.copy(password = password, error = null)
    }

    fun login() {
        if (usuarisAutoritzats.containsKey(uiState.username) && usuarisAutoritzats[uiState.username] == uiState.password) {
            uiState = uiState.copy(isLoggedIn = true, error = null)
        } else {
            uiState = uiState.copy(error = "Usuari o contrasenya incorrectes")
        }
    }
}

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val error: String? = null,
    val isLoggedIn: Boolean = false
)

val usuarisAutoritzats = mapOf(
    "arnau" to "1234", "marta" to "abcd", "carles" to "qwerty",
    "laia" to "zxcv", "pau" to "5678", "anna" to "dcba"
)