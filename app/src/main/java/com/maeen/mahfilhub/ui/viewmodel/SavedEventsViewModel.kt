package com.maeen.mahfilhub.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.maeen.mahfilhub.data.model.SavedEventItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * UI state for screens that consume saved-events data.
 */
data class SavedEventsUiState(
    val savedEvents: List<SavedEventItem> = emptyList(),
    val selectedFilter: String = "All",
    val filters: List<String> = listOf("All", "Upcoming", "Past"),
    val filteredEvents: List<SavedEventItem> = emptyList(),
    val totalSaved: Int = 0,
    val upcomingCount: Int = 0,
    val pastCount: Int = 0
)

/**
 * ViewModel powering SavedEventsScreen.
 *
 * Mirrors the iOS `SavedEventsViewModel` for feature parity.
 * Follows the same StateFlow pattern as other ViewModels.
 */
class SavedEventsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SavedEventsUiState())
    val uiState: StateFlow<SavedEventsUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(savedEvents = seedSavedEvents) }
        recompute()
    }

    // ── Public Actions ───────────────────────────────────────────────────

    fun setFilter(filter: String) {
        _uiState.update { it.copy(selectedFilter = filter) }
        recompute()
    }

    // ── Private ──────────────────────────────────────────────────────────

    private fun recompute() {
        _uiState.update { state ->
            val filtered = when (state.selectedFilter) {
                "Upcoming" -> state.savedEvents.filter { it.isUpcoming }
                "Past" -> state.savedEvents.filter { !it.isUpcoming }
                else -> state.savedEvents
            }
            state.copy(
                filteredEvents = filtered,
                totalSaved = state.savedEvents.size,
                upcomingCount = state.savedEvents.count { it.isUpcoming },
                pastCount = state.savedEvents.count { !it.isUpcoming }
            )
        }
    }

    // ── Seed Data (replace with API call later) ──────────────────────────

    companion object {
        private val seedSavedEvents = listOf(
            SavedEventItem(
                id = 1, title = "Friday Waz Mahfil", maulana = "Maulana Abdul Karim",
                location = "Dhaka Central Mosque, Motijheel", date = "Apr 18, 2026",
                time = "After Jummah", savedDate = "Saved 2 days ago",
                isUpcoming = true, isLive = true, attendees = 245
            ),
            SavedEventItem(
                id = 2, title = "Tafseer Al-Quran", maulana = "Maulana Tariq Jameel",
                location = "Baitul Mukarram National Mosque", date = "Apr 20, 2026",
                time = "After Maghrib", savedDate = "Saved 5 days ago",
                isUpcoming = true, attendees = 180
            ),
            SavedEventItem(
                id = 3, title = "Seerah Conference", maulana = "Maulana Hassan Ali",
                location = "Chittagong Grand Masjid", date = "Apr 25, 2026",
                time = "10:00 AM", savedDate = "Saved 1 week ago",
                isUpcoming = true, attendees = 320
            ),
            SavedEventItem(
                id = 4, title = "Youth Islamic Seminar", maulana = "Maulana Ibrahim Khalil",
                location = "Sylhet Central Eidgah", date = "May 2, 2026",
                time = "3:00 PM", savedDate = "Saved 1 week ago",
                isUpcoming = true, attendees = 150
            ),
            SavedEventItem(
                id = 5, title = "Quran Recitation Night", maulana = "Qari Muhammad Yusuf",
                location = "Rajshahi City Mosque", date = "Mar 22, 2026",
                time = "After Isha", savedDate = "Saved 3 weeks ago",
                isUpcoming = false, attendees = 95, category = "Past"
            ),
            SavedEventItem(
                id = 6, title = "Islamic Finance Workshop", maulana = "Mufti Abdul Rahman",
                location = "BICC, Dhaka", date = "Mar 10, 2026",
                time = "9:00 AM", savedDate = "Saved 1 month ago",
                isUpcoming = false, attendees = 75, category = "Past"
            ),
            SavedEventItem(
                id = 7, title = "Milad-un-Nabi Program", maulana = "Maulana Shah Ahmed",
                location = "Khulna Boro Masjid", date = "Mar 5, 2026",
                time = "After Asr", savedDate = "Saved 1 month ago",
                isUpcoming = false, attendees = 400, category = "Past"
            ),
            SavedEventItem(
                id = 8, title = "Dua & Zikr Evening", maulana = "Maulana Noor Islam",
                location = "Comilla Central Mosque", date = "Feb 28, 2026",
                time = "After Maghrib", savedDate = "Saved 2 months ago",
                isUpcoming = false, attendees = 60, category = "Past"
            ),
            SavedEventItem(
                id = 9, title = "Ramadan Preparation Seminar", maulana = "Maulana Rafiq Ahmed",
                location = "Gulshan Central Mosque", date = "May 10, 2026",
                time = "After Asr", savedDate = "Saved 3 days ago",
                isUpcoming = true, attendees = 200
            ),
            SavedEventItem(
                id = 10, title = "Hadith Study Circle", maulana = "Maulana Ismail Hossain",
                location = "Uttara Jame Masjid", date = "May 15, 2026",
                time = "After Fajr", savedDate = "Saved 1 day ago",
                isUpcoming = true, attendees = 55
            ),
            SavedEventItem(
                id = 11, title = "Family Islamic Gathering", maulana = "Maulana Kamal Uddin",
                location = "Mirpur 10 Masjid", date = "May 20, 2026",
                time = "After Zuhr", savedDate = "Saved today",
                isUpcoming = true, attendees = 130
            ),
            SavedEventItem(
                id = 12, title = "Eid Preparation Mahfil", maulana = "Maulana Zahid Hasan",
                location = "Dhanmondi Eidgah", date = "May 28, 2026",
                time = "10:00 AM", savedDate = "Saved today",
                isUpcoming = true, attendees = 350
            )
        )
    }
}
