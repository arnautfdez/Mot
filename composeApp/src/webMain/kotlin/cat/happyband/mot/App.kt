package cat.happyband.mot

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import cat.happyband.mot.game.ui.GameScreen
import cat.happyband.mot.game.ui.GameViewModel
import cat.happyband.mot.login.ui.LoginScreen
import cat.happyband.mot.login.ui.LoginViewModel
import cat.happyband.mot.ui.theme.MotAppTheme

@Composable
fun App() {
    MotAppTheme {
        val loginViewModel = remember { LoginViewModel() }
        val loginState by remember { derivedStateOf { loginViewModel.uiState } }
        if (!loginState.isLoggedIn) {
            LoginScreen(viewModel = loginViewModel)
        } else {
            val gameViewModel = remember { GameViewModel() }
            GameScreen(
                username = loginState.username,
                viewModel = gameViewModel,
            )
        }
    }
}