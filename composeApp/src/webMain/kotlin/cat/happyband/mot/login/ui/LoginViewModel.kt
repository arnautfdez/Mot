package cat.happyband.mot.login.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cat.happyband.mot.login.data.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel {
    var uiState by mutableStateOf(LoginUiState())
        private set

    private val authRepository = AuthRepository()
    private val viewModelScope = CoroutineScope(Dispatchers.Default)

    fun onUsernameChange(username: String) {
        uiState = uiState.copy(username = username, error = null)
    }

    fun onPasswordChange(password: String) {
        uiState = uiState.copy(password = password, error = null)
    }

    fun onLoginClick() {
        val username = uiState.username.trim()
        val enteredPassword = uiState.password

        if (username.isEmpty() || enteredPassword.isEmpty()) {
            uiState = uiState.copy(error = "El nom d'usuari i la contrasenya són obligatoris.")
            return
        }

        uiState = uiState.copy(error = null)

        viewModelScope.launch {

            val storedHash = authRepository.getUserPassword(username)

            if (storedHash != null && storedHash == enteredPassword) {

                uiState = uiState.copy(
                    isLoggedIn = true,
                    username = username,
                    error = null
                )
            } else if (storedHash != null) {
                uiState = uiState.copy(
                    error = "Contrasenya incorrecta per a l'usuari $username."
                )
            } else {
                uiState = uiState.copy(
                    error = "L'usuari '$username' no està autoritzat o hi ha un error de xarxa."
                )
            }
        }
    }
}

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val error: String? = null,
    val isLoggedIn: Boolean = false
)