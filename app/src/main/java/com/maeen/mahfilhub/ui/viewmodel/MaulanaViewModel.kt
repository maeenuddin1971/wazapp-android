package com.maeen.mahfilhub.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.maeen.mahfilhub.data.model.MaulanaItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * UI state for screens that consume maulana (scholar) data.
 *
 * Holds the raw list, current filter/search values, and all derived
 * data so Composables never compute these themselves.
 */
data class MaulanaUiState(
    val maulanas: List<MaulanaItem> = emptyList(),
    val selectedFilter: String = "All",
    val searchQuery: String = "",
    val filters: List<String> = listOf("All", "Popular", "New", "Verified"),
    val filteredMaulanas: List<MaulanaItem> = emptyList(),
    val featuredMaulanas: List<MaulanaItem> = emptyList(),
    val totalCount: Int = 0,
    val verifiedCount: Int = 0,
    val totalUpcomingEvents: Int = 0
)

/**
 * Shared ViewModel powering HomeScreen (featured maulanas), MaulanaScreen
 * (full list + filter + search), and MaulanaDetailScreen (lookup by ID).
 *
 * Follows the same StateFlow pattern established by [LoginViewModel]
 * and [EventsViewModel]. Mirrors the iOS `MaulanaViewModel` for parity.
 */
class MaulanaViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MaulanaUiState())
    val uiState: StateFlow<MaulanaUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(maulanas = seedMaulanas) }
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

    fun toggleFollow(maulanaId: Int) {
        _uiState.update { state ->
            val updated = state.maulanas.map { maulana ->
                if (maulana.id == maulanaId) {
                    maulana.copy(
                        isFollowing = !maulana.isFollowing,
                        followers = maulana.followers + if (maulana.isFollowing) -1 else 1
                    )
                } else maulana
            }
            state.copy(maulanas = updated)
        }
        recompute()
    }

    // ── Lookup Helpers ───────────────────────────────────────────────────

    fun maulanaById(id: Int): MaulanaItem? {
        return _uiState.value.maulanas.find { it.id == id }
    }

    // ── Private ──────────────────────────────────────────────────────────

    private fun recompute() {
        _uiState.update { state ->
            val filtered = state.maulanas.filter { maulana ->
                val matchesFilter = when (state.selectedFilter) {
                    "All" -> true
                    "Verified" -> maulana.isVerified
                    else -> maulana.category == state.selectedFilter
                }
                val matchesSearch = state.searchQuery.isEmpty() ||
                    maulana.name.contains(state.searchQuery, ignoreCase = true) ||
                    maulana.specialization.contains(state.searchQuery, ignoreCase = true) ||
                    maulana.location.contains(state.searchQuery, ignoreCase = true)
                matchesFilter && matchesSearch
            }
            state.copy(
                filteredMaulanas = filtered,
                featuredMaulanas = state.maulanas.take(5),
                totalCount = state.maulanas.size,
                verifiedCount = state.maulanas.count { it.isVerified },
                totalUpcomingEvents = state.maulanas.sumOf { it.upcomingEvents }
            )
        }
    }

    // ── Seed Data (replace with API call later) ──────────────────────────

    companion object {
        private val seedMaulanas = listOf(
            MaulanaItem(
                id = 1, name = "Maulana Abdul Karim", title = "Senior Scholar",
                specialization = "Tafseer & Hadith", location = "Dhaka, Bangladesh",
                totalEvents = 120, upcomingEvents = 3, followers = 4520, rating = 4.9f,
                isVerified = true, category = "Popular"
            ),
            MaulanaItem(
                id = 2, name = "Maulana Tariq Jameel", title = "International Speaker",
                specialization = "Dawah & Islah", location = "Lahore, Pakistan",
                totalEvents = 85, upcomingEvents = 2, followers = 12800, rating = 4.8f,
                isVerified = true, category = "Popular"
            ),
            MaulanaItem(
                id = 3, name = "Maulana Hassan Ali", title = "Quran Teacher",
                specialization = "Tafseer Al-Quran", location = "Chittagong, Bangladesh",
                totalEvents = 64, upcomingEvents = 1, followers = 2150, rating = 4.7f,
                isVerified = false, category = "Popular"
            ),
            MaulanaItem(
                id = 4, name = "Maulana Ibrahim Khalil", title = "Youth Mentor",
                specialization = "Youth & Contemporary Issues", location = "Sylhet, Bangladesh",
                totalEvents = 42, upcomingEvents = 2, followers = 1800, rating = 4.6f,
                isVerified = false, category = "New"
            ),
            MaulanaItem(
                id = 5, name = "Qari Muhammad Yusuf", title = "Hafiz & Qari",
                specialization = "Quran Recitation & Tajweed", location = "Rajshahi, Bangladesh",
                totalEvents = 35, upcomingEvents = 1, followers = 980, rating = 4.9f,
                isVerified = true, category = "New"
            ),
            MaulanaItem(
                id = 6, name = "Mufti Abdul Rahman", title = "Islamic Finance Expert",
                specialization = "Fiqh & Islamic Finance", location = "Dhaka, Bangladesh",
                totalEvents = 28, upcomingEvents = 0, followers = 1450, rating = 4.5f,
                isVerified = true, category = "Popular"
            ),
            MaulanaItem(
                id = 7, name = "Maulana Shah Ahmed", title = "Community Leader",
                specialization = "Seerah & History", location = "Khulna, Bangladesh",
                totalEvents = 55, upcomingEvents = 2, followers = 3200, rating = 4.7f,
                isVerified = false, category = "Popular"
            ),
            MaulanaItem(
                id = 8, name = "Maulana Noor Islam", title = "Spiritual Guide",
                specialization = "Tasawwuf & Zikr", location = "Comilla, Bangladesh",
                totalEvents = 30, upcomingEvents = 1, followers = 890, rating = 4.4f,
                isVerified = false, category = "New"
            ),
            MaulanaItem(
                id = 9, name = "Maulana Fazlur Rahman", title = "Hadith Scholar",
                specialization = "Sahih Bukhari & Muslim", location = "Barisal, Bangladesh",
                totalEvents = 48, upcomingEvents = 0, followers = 2600, rating = 4.8f,
                isVerified = true, category = "Popular"
            ),
            MaulanaItem(
                id = 10, name = "Maulana Yusuf Ali", title = "Education Specialist",
                specialization = "Islamic Education & Tarbiyah", location = "Rangpur, Bangladesh",
                totalEvents = 22, upcomingEvents = 1, followers = 720, rating = 4.3f,
                isVerified = false, category = "New"
            )
        )
    }
}
