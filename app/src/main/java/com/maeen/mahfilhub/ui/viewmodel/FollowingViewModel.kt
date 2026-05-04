package com.maeen.mahfilhub.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.maeen.mahfilhub.data.model.FollowedScholar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * UI state for screens that consume followed-scholars data.
 */
data class FollowingUiState(
    val scholars: List<FollowedScholar> = emptyList(),
    val totalFollowing: Int = 0,
    val withUpcomingCount: Int = 0,
    val totalUpcomingEvents: Int = 0
)

/**
 * ViewModel powering FollowingScreen.
 *
 * Mirrors the iOS `FollowingViewModel` for feature parity.
 * Follows the same StateFlow pattern as other ViewModels.
 */
class FollowingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FollowingUiState())
    val uiState: StateFlow<FollowingUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(scholars = seedScholars) }
        recompute()
    }

    // ── Private ──────────────────────────────────────────────────────────

    private fun recompute() {
        _uiState.update { state ->
            state.copy(
                totalFollowing = state.scholars.size,
                withUpcomingCount = state.scholars.count { it.upcomingEvents > 0 },
                totalUpcomingEvents = state.scholars.sumOf { it.upcomingEvents }
            )
        }
    }

    // ── Seed Data (replace with API call later) ──────────────────────────

    companion object {
        private val seedScholars = listOf(
            FollowedScholar(
                id = 1, name = "Maulana Abdul Karim", title = "Senior Islamic Scholar",
                location = "Dhaka, Bangladesh", followedSince = "Following since Jan 2026",
                upcomingEvents = 3, totalEvents = 45, isVerified = true
            ),
            FollowedScholar(
                id = 2, name = "Maulana Tariq Jameel", title = "International Speaker",
                location = "Lahore, Pakistan", followedSince = "Following since Mar 2025",
                upcomingEvents = 1, totalEvents = 120, isVerified = true
            ),
            FollowedScholar(
                id = 3, name = "Maulana Hassan Ali", title = "Quran Scholar",
                location = "Chittagong, Bangladesh", followedSince = "Following since Jun 2025",
                upcomingEvents = 2, totalEvents = 30, isVerified = true
            ),
            FollowedScholar(
                id = 4, name = "Maulana Ibrahim Khalil", title = "Youth Motivational Speaker",
                location = "Sylhet, Bangladesh", followedSince = "Following since Sep 2025",
                upcomingEvents = 0, totalEvents = 18
            ),
            FollowedScholar(
                id = 5, name = "Qari Muhammad Yusuf", title = "Hafiz & Qari",
                location = "Rajshahi, Bangladesh", followedSince = "Following since Nov 2025",
                upcomingEvents = 1, totalEvents = 22, isVerified = true
            ),
            FollowedScholar(
                id = 6, name = "Mufti Abdul Rahman", title = "Islamic Finance Expert",
                location = "Dhaka, Bangladesh", followedSince = "Following since Dec 2025",
                upcomingEvents = 0, totalEvents = 15
            ),
            FollowedScholar(
                id = 7, name = "Maulana Shah Ahmed", title = "Hadith Scholar",
                location = "Khulna, Bangladesh", followedSince = "Following since Feb 2026",
                upcomingEvents = 2, totalEvents = 55, isVerified = true
            ),
            FollowedScholar(
                id = 8, name = "Maulana Noor Islam", title = "Tafseer Specialist",
                location = "Comilla, Bangladesh", followedSince = "Following since Mar 2026",
                upcomingEvents = 1, totalEvents = 28
            )
        )
    }
}
