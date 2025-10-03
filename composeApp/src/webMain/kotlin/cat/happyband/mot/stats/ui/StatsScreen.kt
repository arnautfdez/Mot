package cat.happyband.mot.stats.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cat.happyband.mot.stats.domain.GlobalRankItem
import cat.happyband.mot.stats.domain.PersonalStats
import cat.happyband.mot.stats.domain.StatsUiState
import cat.happyband.mot.utils.roundToDecimals

@Composable
fun StatsScreen(currentUser: String) {
    val viewModel = remember { StatsViewModel(currentUser) }
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {

        item {
            Text(
                "Estadístiques del Mot",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(16.dp))
        }

        item {
            Text("El meu Rendiment", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            PersonalStatsCard(stats = uiState.personalStats)
            Spacer(Modifier.height(32.dp))
        }

        item {
            Text("Distribució d'Intents", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            IntentDistributionChart(distribution = uiState.personalStats.distribution)
            Spacer(Modifier.height(32.dp))
        }

        item {
            Text("Rànquing Global", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            RankingHeader()
        }

        items(uiState.ranking) { item ->
            RankingRow(item, isCurrentUser = item.username == currentUser)
            Divider(color = Color.LightGray)
        }
    }
}

@Composable
fun PersonalStatsCard(stats: PersonalStats) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceAround) {
            StatItem("Jugades", stats.gamesPlayed.toString())
            StatItem("Intents Mitjans", stats.averageGuesses.roundToDecimals(2).toString())
        }
    }
}

@Composable
fun StatItem(title: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 8.dp),
    ) {
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(title, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}

@Composable
fun RankingHeader() {
    Row(Modifier.fillMaxWidth().background(Color.LightGray).padding(8.dp)) {
        Text("#", Modifier.weight(0.5f), fontWeight = FontWeight.Bold)
        Text("Usuari", Modifier.weight(3f), fontWeight = FontWeight.Bold)
        Text("Punts", Modifier.weight(1.5f), fontWeight = FontWeight.Bold)
        Text("Mitjana Int.", Modifier.weight(2f), fontWeight = FontWeight.Bold)
    }
}

@Composable
fun RankingRow(item: GlobalRankItem, isCurrentUser: Boolean) {
    val bgColor =
        if (isCurrentUser) Color(0xFFFFF0B3) else Color.Transparent // Destaca l'usuari actual

    Row(Modifier.fillMaxWidth().background(bgColor).padding(8.dp)) {
        Text(item.rank.toString(), Modifier.weight(0.5f), fontWeight = FontWeight.SemiBold)
        Text(
            item.username,
            Modifier.weight(3f),
            fontWeight = if (isCurrentUser) FontWeight.ExtraBold else FontWeight.Normal
        )
        Text(item.score.toString(), Modifier.weight(1.5f))
        Text(item.averageGuesses.roundToDecimals(2).toString(), Modifier.weight(2f))
    }
}

@Composable
fun IntentDistributionChart(distribution: List<Int>) {
    val maxWins = distribution.maxOrNull() ?: 1
    val baseColor = Color(0xFF6AAA64)
    val totalAttempts = 6

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            (1..totalAttempts).forEach { attempts ->
                val winsForAttempt = distribution.getOrNull(attempts - 1) ?: 0

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = attempts.toString(),
                        modifier = Modifier.width(20.dp),
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )

                    Spacer(Modifier.width(4.dp))

                    val barWidthFraction =
                        if (maxWins > 0) winsForAttempt.toFloat() / maxWins.toFloat() else 0f

                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(fraction = barWidthFraction)
                                .background(
                                    color = baseColor,
                                    shape = RoundedCornerShape(
                                        topStart = 0.dp,
                                        bottomStart = 0.dp,
                                        topEnd = 16.dp,
                                        bottomEnd = 16.dp,
                                    )
                                )
                                .padding(horizontal = 4.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text(
                                text = winsForAttempt.toString(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}


fun createDummyStats(currentUser: String): StatsUiState {

    val ranking = listOf(
        GlobalRankItem(
            rank = 1,
            username = "Arnau",
            score = 1500,
            averageGuesses = 3.2.roundToDecimals(2),
        ),
        GlobalRankItem(
            rank = 2,
            username = "Laura",
            score = 1200,
            averageGuesses = 3.5.roundToDecimals(2),
        ),
        GlobalRankItem(
            rank = 3,
            username = "Pere",
            score = 800,
            averageGuesses = 4.1.roundToDecimals(2),
        ),
        GlobalRankItem(
            rank = 4,
            username = "Maria",
            score = 600,
            averageGuesses = 4.8.roundToDecimals(2),
        ),
    ).sortedByDescending { it.score }

    val personal = PersonalStats(
        gamesPlayed = 12,
        averageGuesses = 3.4.roundToDecimals(2),
        distribution = listOf(1, 2, 4, 3, 0, 2)
    )

    return StatsUiState(ranking = ranking, personalStats = personal)
}