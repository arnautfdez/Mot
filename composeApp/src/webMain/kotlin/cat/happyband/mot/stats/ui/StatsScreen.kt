package cat.happyband.mot.stats.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class UserStats(
    val username: String,
    val score: Int,
    val wins: Int,
    val guesses: Double
)

@Composable
fun StatsScreen() {
    val dummyRanking = remember {
        listOf(
            UserStats("Arnau", 1500, 10, 3.2),
            UserStats("Laura", 1200, 8, 3.5),
            UserStats("Pere", 800, 5, 4.1)
        ).sortedByDescending { it.score }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Ranking Global", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(16.dp))

        Row(Modifier.fillMaxWidth().background(Color.LightGray).padding(vertical = 8.dp)) {
            Text("Usuari", Modifier.weight(3f))
            Text("Punts", Modifier.weight(1.5f))
            Text("Victòries", Modifier.weight(2f))
        }

        Spacer(Modifier.height(4.dp))

        LazyColumn {
            items(dummyRanking) { stats ->
                RankingRow(stats)
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun RankingRow(stats: UserStats) {
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(stats.username, Modifier.weight(3f))
        Text(stats.score.toString(), Modifier.weight(1.5f))
        Text(stats.wins.toString(), Modifier.weight(2f))
    }
}