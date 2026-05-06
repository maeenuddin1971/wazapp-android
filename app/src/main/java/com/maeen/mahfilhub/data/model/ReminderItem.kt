package com.maeen.mahfilhub.data.model

data class ReminderItem(
    val id: Int,
    val eventTitle: String,
    val maulana: String,
    val date: String,
    val time: String,
    val location: String,
    val reminderTime: String,
    val daysUntil: Int = 0
)
