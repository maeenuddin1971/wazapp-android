package com.maeen.mahfilhub.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.maeen.mahfilhub.data.model.MaulanaItem
import com.maeen.mahfilhub.data.repository.MaulanaRepository
import com.maeen.mahfilhub.data.repository.MaulanaRepository.Companion.toDomain
import com.maeen.mahfilhub.util.Resource
import com.maeen.mahfilhub.util.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI state for screens that consume maulana (scholar) data.
 */
data class MaulanaUiState(
    val isLoading: Boolean = false,
    val maulanas: List<MaulanaItem> = emptyList(),
    val selectedFilter: String = "All",
    val searchQuery: String = "",
    val filters: List<String> = listOf("All", "Popular", "New", "Verified"),
    val filteredMaulanas: List<MaulanaItem> = emptyList(),
    val featuredMaulanas: List<MaulanaItem> = emptyList(),
    val totalCount: Int = 0,
    val verifiedCount: Int = 0,
    val totalUpcomingEvents: Int = 0,
    val errorMessage: String? = null,
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val isLastPage: Boolean = false
)

/**
 * Shared ViewModel powering HomeScreen (featured maulanas), MaulanaScreen
 * (full list + filter + search), and MaulanaDetailScreen (lookup by ID).
 *
 * Fetches data from GET /maolana/list API.
 * Token is handled by AuthInterceptor automatically.
 */
class MaulanaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MaulanaRepository()

    private val _uiState = MutableStateFlow(MaulanaUiState())
    val uiState: StateFlow<MaulanaUiState> = _uiState.asStateFlow()

    init {
        loadMaulanas()
    }

    // ── Public Actions ───────────────────────────────────────────────────

    fun loadMaulanas(page: Int = 0) {
        val context = getApplication<Application>()
        val token = SessionManager.getToken(context)
        Log.d(TAG, "loadMaulanas: token=${if (token.isNullOrBlank()) "NULL/BLANK" else "present (${token.length} chars)"}")

        if (token.isNullOrBlank()) {
            Log.w(TAG, "loadMaulanas: No token found, user not logged in or guest mode")
            _uiState.update {
                it.copy(isLoading = false, errorMessage = "Please log in to view scholars")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            Log.d(TAG, "loadMaulanas: Calling API page=$page")

            when (val result = repository.getMaulanaList(token, page)) {
                is Resource.Success -> {
                    val response = result.data
                    val apiItems = response.data.content
                    val domainItems = apiItems.map { it.toDomain() }
                    Log.d(TAG, "loadMaulanas: SUCCESS - got ${apiItems.size} items, totalElements=${response.data.totalElements}")

                    _uiState.update { state ->
                        val allMaulanas = if (page == 0) {
                            domainItems
                        } else {
                            state.maulanas + domainItems
                        }
                        state.copy(
                            isLoading = false,
                            maulanas = allMaulanas,
                            currentPage = response.data.page,
                            totalPages = response.data.totalPages,
                            isLastPage = response.data.isLast
                        )
                    }
                    recompute()
                }
                is Resource.Error -> {
                    Log.e(TAG, "loadMaulanas: ERROR - ${result.message}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
                is Resource.Loading -> { /* handled by isLoading flag */ }
            }
        }
    }

    fun loadNextPage() {
        val state = _uiState.value
        if (!state.isLoading && !state.isLastPage) {
            loadMaulanas(state.currentPage + 1)
        }
    }

    fun setFilter(filter: String) {
        _uiState.update { it.copy(selectedFilter = filter) }
        recompute()
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        recompute()
    }

    fun toggleFollow(maulanaId: String) {
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

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    // ── Lookup Helpers ───────────────────────────────────────────────────

    fun maulanaById(id: String): MaulanaItem? {
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

    companion object {
        private const val TAG = "MaulanaViewModel"
    }
}
