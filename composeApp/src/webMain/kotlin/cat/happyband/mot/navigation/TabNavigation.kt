package cat.happyband.mot.navigation

import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun TabNavigation(onScreenSelected: (MotScreen) -> Unit) {

    var selectedTabIndex by remember { mutableStateOf(0) }

    val screens = listOf(
        MotScreen.GAME to "Joc",
        MotScreen.STATS to "Estadístiques"
    )

    TabRow(selectedTabIndex = selectedTabIndex) {
        screens.forEachIndexed { index, screenPair ->
            val screen = screenPair.first
            val title = screenPair.second

            Tab(
                selected = selectedTabIndex == index,

                onClick = {
                    selectedTabIndex = index
                    onScreenSelected(screen)
                },

                text = { Text(title) }
            )
        }
    }
}