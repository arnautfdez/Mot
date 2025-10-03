package cat.happyband.mot.stats.ui

import cat.happyband.mot.stats.data.StatsRepository
import cat.happyband.mot.stats.domain.StatsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class StatsViewModel(private val currentUser: String) {

    private val repository = StatsRepository()
    private val viewModelScope = CoroutineScope(Dispatchers.Default)

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val calculatedRanking = repository.getAndCalculateGlobalRanking()

                val calculatedPersonalStats = repository.getPersonalStats(currentUser)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    ranking = calculatedRanking,
                    personalStats = calculatedPersonalStats
                )
            } catch (e: Exception) {
                println("ERROR loading stats from Supabase: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    ranking = emptyList()
                )
            }
        }
    }
}