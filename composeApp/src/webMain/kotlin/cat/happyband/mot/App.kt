package cat.happyband.mot

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cat.happyband.mot.game.ui.GameScreen
import cat.happyband.mot.game.ui.GameViewModel
import cat.happyband.mot.login.ui.LoginScreen
import cat.happyband.mot.login.ui.LoginViewModel
import cat.happyband.mot.navigation.MotScreen
import cat.happyband.mot.navigation.TabNavigation
import cat.happyband.mot.stats.ui.StatsScreen
import cat.happyband.mot.ui.theme.MotAppTheme

@Composable
fun App() {
    MotAppTheme {
        val loginViewModel = remember { LoginViewModel() }
        val loginState by remember { derivedStateOf { loginViewModel.uiState } }
        var currentScreen by remember { mutableStateOf(MotScreen.GAME) }

        if (!loginState.isLoggedIn) {
            LoginScreen(viewModel = loginViewModel)
        } else {
            Column {
                TabNavigation(onScreenSelected = { currentScreen = it })
                val gameViewModel = remember { GameViewModel() }
                when (currentScreen) {
                    MotScreen.GAME -> {
                        GameScreen(
                            username = loginState.username,
                            viewModel = gameViewModel,
                        )
                    }
                    MotScreen.STATS -> {
                        StatsScreen()
                    }
                }
            }
        }
    }
}