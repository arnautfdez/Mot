package cat.happyband.mot.stats.data

import cat.happyband.mot.game.data.GameRepository
import cat.happyband.mot.stats.domain.GlobalRankItem
import cat.happyband.mot.stats.domain.PersonalStats
import cat.happyband.mot.utils.roundToDecimals

class StatsRepository {

    private val gameDataSource = GameRepository()

    suspend fun getAndCalculateGlobalRanking(): List<GlobalRankItem> {
        val allResults = gameDataSource.fetchAllGameResults()
        if (allResults.isEmpty()) return emptyList()

        val userGroups = allResults.groupBy { it.username }

        val rankingList = userGroups.map { (username, results) ->

            val totalScore = results.sumOf { it.score }
            val wonGames = results.count { it.solved }
            val totalAttempts = results.filter { it.solved }.sumOf { it.attempts }
            val averageGuesses = if (wonGames > 0) totalAttempts.toDouble() / wonGames else 0.0

            GlobalRankItem(
                rank = 0,
                username = username,
                score = totalScore,
                averageGuesses = averageGuesses.roundToDecimals(2)
            )
        }

        return rankingList
            .sortedByDescending { it.score }
            .mapIndexed { index, item -> item.copy(rank = index + 1) }
    }

    suspend fun getPersonalStats(currentUser: String): PersonalStats {
        val allResults = gameDataSource.fetchAllGameResults()
        val personalResults = allResults.filter { it.username == currentUser }

        if (personalResults.isEmpty()) return PersonalStats(0, 0.0, List(6) { 0 }) // <-- Constructor ajustat

        val wonGames = personalResults.count { it.solved }
        val totalAttempts = personalResults.filter { it.solved }.sumOf { it.attempts }
        val averageGuesses = if (wonGames > 0) totalAttempts.toDouble() / wonGames else 0.0

        val distribution = (1..6).map { attempts ->
            personalResults.count { it.solved && it.attempts == attempts }
        }

        return PersonalStats(
            gamesPlayed = personalResults.size,
            averageGuesses = averageGuesses.roundToDecimals(2),
            distribution = distribution
        )
    }

}