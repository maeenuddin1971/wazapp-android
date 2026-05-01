package com.maeen.mahfilhub.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.maeen.mahfilhub.data.model.EventItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * UI state for screens that consume event data.
 *
 * Holds the raw events list, current filter/search values, and all derived
 * data so Composables never compute these themselves.
 */
data class EventsUiState(
    val events: List<EventItem> = emptyList(),
    val selectedFilter: String = "All",
    val searchQuery: String = "",
    val filters: List<String> = listOf("All", "Today", "This Week", "This Month"),
    val filteredEvents: List<EventItem> = emptyList(),
    val liveCount: Int = 0,
    val upcomingEvents: List<EventItem> = emptyList()
)

/**
 * Shared ViewModel powering HomeScreen (upcoming events), EventsScreen
 * (full list + filter + search), and EventDetailScreen (lookup by ID).
 *
 * Follows the same StateFlow pattern established by [LoginViewModel].
 * Seed data matches the iOS `EventsViewModel` for feature parity.
 */
class EventsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EventsUiState())
    val uiState: StateFlow<EventsUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(events = seedEvents) }
        recompute()
    }

    // ── Public Actions ───────────────────────────────────────────────────

    fun setFilter(filter: String) {
        _uiState.update { it.copy(selectedFilter = filter) }
        recompute()
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        recompute()
    }

    // ── Lookup Helpers ───────────────────────────────────────────────────

    fun eventById(id: Int): EventItem? {
        return _uiState.value.events.find { it.id == id }
    }

    fun eventsForMaulana(name: String): List<EventItem> {
        return _uiState.value.events.filter { it.maulana == name }
    }

    // ── Private ──────────────────────────────────────────────────────────

    private fun recompute() {
        _uiState.update { state ->
            val filtered = state.events.filter { event ->
                val matchesFilter =
                    state.selectedFilter == "All" || event.category == state.selectedFilter
                val matchesSearch = state.searchQuery.isEmpty() ||
                    event.title.contains(state.searchQuery, ignoreCase = true) ||
                    event.maulana.contains(state.searchQuery, ignoreCase = true) ||
                    event.location.contains(state.searchQuery, ignoreCase = true)
                matchesFilter && matchesSearch
            }
            state.copy(
                filteredEvents = filtered,
                liveCount = filtered.count { it.isLive },
                upcomingEvents = state.events.take(5)
            )
        }
    }

    // ── Seed Data (replace with API call later) ──────────────────────────

    companion object {
        private val seedEvents = listOf(
            EventItem(
                id = 1,
                title = "Friday Waz Mahfil",
                maulana = "Maulana Abdul Karim",
                location = "Dhaka Central Mosque, Motijheel",
                date = "Mar 14, 2026",
                time = "After Jummah",
                isLive = true,
                isFeatured = true,
                attendees = 245,
                category = "Today"
            ),
            EventItem(
                id = 2,
                title = "Tafseer Al-Quran",
                maulana = "Maulana Tariq Jameel",
                location = "Baitul Mukarram National Mosque",
                date = "Mar 15, 2026",
                time = "After Maghrib",
                isFeatured = true,
                attendees = 180,
                category = "This Week"
            ),
            EventItem(
                id = 3,
                title = "Seerah Conference",
                maulana = "Maulana Hassan Ali",
                location = "Chittagong Grand Masjid",
                date = "Mar 18, 2026",
                time = "10:00 AM",
                attendees = 320,
                category = "This Week"
            ),
            EventItem(
                id = 4,
                title = "Youth Islamic Seminar",
                maulana = "Maulana Ibrahim Khalil",
                location = "Sylhet Central Eidgah",
                date = "Mar 20, 2026",
                time = "3:00 PM",
                attendees = 150,
                category = "This Month"
            ),
            EventItem(
                id = 5,
                title = "Quran Recitation Night",
                maulana = "Qari Muhammad Yusuf",
                location = "Rajshahi City Mosque",
                date = "Mar 22, 2026",
                time = "After Isha",
                attendees = 95,
                category = "This Month"
            ),
            EventItem(
                id = 6,
                title = "Islamic Finance Workshop",
                maulana = "Mufti Abdul Rahman",
                location = "BICC, Dhaka",
                date = "Mar 25, 2026",
                time = "9:00 AM",
                attendees = 75,
                category = "This Month"
            ),
            EventItem(
                id = 7,
                title = "Milad-un-Nabi Program",
                maulana = "Maulana Shah Ahmed",
                location = "Khulna Boro Masjid",
                date = "Mar 28, 2026",
                time = "After Asr",
                isFeatured = true,
                attendees = 400,
                category = "This Month"
            ),
            EventItem(
                id = 8,
                title = "Dua & Zikr Evening",
                maulana = "Maulana Noor Islam",
                location = "Comilla Central Mosque",
                date = "Mar 14, 2026",
                time = "After Maghrib",
                attendees = 60,
                category = "Today"
            )
        )
    }
}
