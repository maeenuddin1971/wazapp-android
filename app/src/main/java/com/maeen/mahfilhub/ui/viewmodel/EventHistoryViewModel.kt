package com.maeen.mahfilhub.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.maeen.mahfilhub.data.model.HistoryEventItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class EventHistoryUiState(
    val events: List<HistoryEventItem> = emptyList(),
    val totalEvents: Int = 0,
    val averageRating: Float = 0f
)

class EventHistoryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EventHistoryUiState())
    val uiState: StateFlow<EventHistoryUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(events = seedEvents) }
        recompute()
    }

    private fun recompute() {
        _uiState.update { state ->
            state.copy(
                totalEvents = state.events.size,
                averageRating = if (state.events.isEmpty()) 0f else state.events.map { it.rating }.average().toFloat()
            )
        }
    }

    companion object {
        private val seedEvents = listOf(
            HistoryEventItem(1, "Quran Recitation Night", "Qari Muhammad Yusuf", "Rajshahi City Mosque", "Mar 22, 2026", 95, 4.8f),
            HistoryEventItem(2, "Islamic Finance Workshop", "Mufti Abdul Rahman", "BICC, Dhaka", "Mar 10, 2026", 75, 4.5f),
            HistoryEventItem(3, "Milad-un-Nabi Program", "Maulana Shah Ahmed", "Khulna Boro Masjid", "Mar 5, 2026", 400, 4.9f),
            HistoryEventItem(4, "Dua & Zikr Evening", "Maulana Noor Islam", "Comilla Central Mosque", "Feb 28, 2026", 60, 4.3f),
            HistoryEventItem(5, "Friday Waz Mahfil", "Maulana Abdul Karim", "Dhaka Central Mosque", "Feb 14, 2026", 230, 4.7f),
            HistoryEventItem(6, "Youth Islamic Seminar", "Maulana Ibrahim Khalil", "Sylhet Central Eidgah", "Jan 20, 2026", 150, 4.6f)
        )
    }
}
