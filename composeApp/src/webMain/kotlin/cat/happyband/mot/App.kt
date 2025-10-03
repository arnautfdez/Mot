package cat.happyband.mot

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import cat.happyband.mot.ui.login.LoginScreen
import cat.happyband.mot.ui.login.LoginViewModel
import cat.happyband.mot.ui.theme.MotAppTheme

@Composable
fun App() {
    MotAppTheme {
        val loginViewModel = remember { LoginViewModel() }
        val loginState by remember { derivedStateOf { loginViewModel.uiState } }
        if (!loginState.isLoggedIn) {
            LoginScreen(viewModel = loginViewModel)
        } else {
            Text("Hello")
        }
    }
}