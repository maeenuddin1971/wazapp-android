package com.maeen.mahfilhub.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.maeen.mahfilhub.data.model.ReminderItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class MyRemindersUiState(
    val reminders: List<ReminderItem> = emptyList(),
    val totalReminders: Int = 0
)

class MyRemindersViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MyRemindersUiState())
    val uiState: StateFlow<MyRemindersUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(reminders = seedReminders) }
        recompute()
    }

    private fun recompute() {
        _uiState.update { state ->
            state.copy(totalReminders = state.reminders.size)
        }
    }

    companion object {
        private val seedReminders = listOf(
            ReminderItem(1, "Friday Waz Mahfil", "Maulana Abdul Karim", "Apr 18, 2026", "After Jummah", "Dhaka Central Mosque", "1 hour before", 5),
            ReminderItem(2, "Tafseer Al-Quran", "Maulana Tariq Jameel", "Apr 20, 2026", "After Maghrib", "Baitul Mukarram", "30 min before", 7),
            ReminderItem(3, "Seerah Conference", "Maulana Hassan Ali", "Apr 25, 2026", "10:00 AM", "Chittagong Grand Masjid", "1 day before", 12)
        )
    }
}
