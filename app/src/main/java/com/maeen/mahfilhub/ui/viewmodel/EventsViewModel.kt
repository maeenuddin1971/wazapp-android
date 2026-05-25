package com.maeen.mahfilhub.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maeen.mahfilhub.data.model.EventItem
import com.maeen.mahfilhub.data.model.MahfilListRequest
import com.maeen.mahfilhub.data.repository.MahfilRepository
import com.maeen.mahfilhub.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
    val upcomingEvents: List<EventItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val hasMore: Boolean = false
)

/**
 * Shared ViewModel powering HomeScreen (upcoming events), EventsScreen
 * (full list + filter + search), and EventDetailScreen (lookup by ID).
 *
 * Fetches mahfils from POST /mahfil/list on init.
 * Falls back to seed data if the API call fails.
 */
class EventsViewModel : ViewModel() {

    private val mahfilRepository = MahfilRepository()

    private val _uiState = MutableStateFlow(EventsUiState())
    val uiState: StateFlow<EventsUiState> = _uiState.asStateFlow()

    init {
        loadMahfils()
    }

    // ── Public Actions ───────────────────────────────────────────────────

    fun loadMahfils(page: Int = 0) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val request = MahfilListRequest(
                maolanaName = "",
                page = page,
                size = 20
            )

            when (val result = mahfilRepository.getMahfilList(request)) {
                is Resource.Success -> {
                    val pageData = result.data
                    val newEvents = pageData.content.map { it.toEventItem() }

                    _uiState.update { state ->
                        val allEvents = if (page == 0) {
                            newEvents
                        } else {
                            state.events + newEvents
                        }
                        state.copy(
                            events = allEvents,
                            isLoading = false,
                            currentPage = pageData.number,
                            totalPages = pageData.totalPages,
                            hasMore = !pageData.last
                        )
                    }
                    recompute()
                }
                is Resource.Error -> {
                    // If first page fails and no events loaded, use seed data
                    if (page == 0 && _uiState.value.events.isEmpty()) {
                        _uiState.update {
                            it.copy(
                                events = seedEvents,
                                isLoading = false,
                                errorMessage = result.message
                            )
                        }
                        recompute()
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = result.message
                            )
                        }
                    }
                }
                is Resource.Loading -> { /* handled by isLoading flag */ }
            }
        }
    }

    fun loadMore() {
        val state = _uiState.value
        if (!state.isLoading && state.hasMore) {
            loadMahfils(page = state.currentPage + 1)
        }
    }

    fun refresh() {
        loadMahfils(page = 0)
    }

    fun setFilter(filter: String) {
        _uiState.update { it.copy(selectedFilter = filter) }
        recompute()
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        recompute()
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
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

    // ── Seed Data (offline fallback) ─────────────────────────────────────

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
            )
        )
    }
}
