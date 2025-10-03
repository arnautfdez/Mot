package cat.happyband.mot.stats.domain

data class GlobalRankItem(
    val rank: Int,
    val username: String,
    val score: Int,         // Puntuació Total (Intents + Temps)
    val averageGuesses: Double,
)

data class PersonalStats(
    val gamesPlayed: Int,
    val averageGuesses: Double,
    // Distribució d'intents: [Guanyats en 1, en 2, en 3, en 4, en 5, en 6]
    val distribution: List<Int>
)

data class StatsUiState(
    val isLoading: Boolean = false,
    val personalStats: PersonalStats = PersonalStats(0, 0.0, List(6) { 0 }),
    val ranking: List<GlobalRankItem> = emptyList()
)