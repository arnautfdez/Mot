package cat.happyband.mot.game.domain

import kotlinx.serialization.Serializable

@Serializable
data class GameResult(
    val id: Int? = null,
    val username: String,
    val solved: Boolean,
    val attempts: Int,
    val score: Int,
    val timeSpentSeconds: Long
)
