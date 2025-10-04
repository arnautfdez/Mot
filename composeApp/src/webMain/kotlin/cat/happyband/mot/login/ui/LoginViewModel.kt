package cat.happyband.mot.login.ui

import cat.happyband.mot.login.data.AuthRepository
import cat.happyband.mot.login.data.getSessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val authRepository = AuthRepository()
    private val viewModelScope = CoroutineScope(Dispatchers.Default)

    private val sessionManager = getSessionManager()

    init {
        val savedUsername = sessionManager.getSession()
        if (savedUsername != null) {
            _uiState.value = _uiState.value.copy(
                username = savedUsername,
                isLoggedIn = true
            )
        }
    }

    fun onUsernameChange(username: String) {
        _uiState.value = _uiState.value.copy(username = username, error = null)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password, error = null)
    }

    fun onLoginClick() {
        val username = _uiState.value.username.trim()
        val enteredPassword = _uiState.value.password

        if (username.isEmpty() || enteredPassword.isEmpty()) {
            _uiState.value = _uiState.value.copy(error = "El nom d'usuari i la contrasenya són obligatoris.")
            return
        }

        _uiState.value = _uiState.value.copy(error = null)

        viewModelScope.launch {

            val storedHash = authRepository.getUserPassword(username)

            if (storedHash != null && storedHash == enteredPassword) {

                sessionManager.saveSession(username)

                _uiState.value = _uiState.value.copy(
                    isLoggedIn = true,
                    username = username,
                    error = null
                )
            } else if (storedHash != null) {
                _uiState.value = _uiState.value.copy(
                    error = "Contrasenya incorrecta per a l'usuari $username."
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    error = "L'usuari '$username' no està autoritzat o hi ha un error de xarxa."
                )
            }
        }
    }

    fun logout() {
        sessionManager.clearSession()
        _uiState.value = _uiState.value.copy(isLoggedIn = false)
    }
}

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val error: String? = null,
    val isLoggedIn: Boolean = false
)