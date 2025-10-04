package cat.happyband.mot

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
        val loginState by loginViewModel.uiState.collectAsState()
        var currentScreen by remember { mutableStateOf(MotScreen.GAME) }

        if (!loginState.isLoggedIn) {
            LoginScreen(viewModel = loginViewModel)
        } else {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {
                        loginViewModel.logout()
                    }) {
                        Text("SORTIR", color = Color.Gray, fontWeight = FontWeight.SemiBold)
                    }
                }
                TabNavigation(onScreenSelected = { currentScreen = it })
                val gameViewModel = remember { GameViewModel(loginState.username) }
                when (currentScreen) {
                    MotScreen.GAME -> {
                        GameScreen(
                            username = loginState.username,
                            viewModel = gameViewModel,
                        )
                    }
                    MotScreen.STATS -> {
                        StatsScreen(loginState.username)
                    }
                }
            }
        }
    }
}
