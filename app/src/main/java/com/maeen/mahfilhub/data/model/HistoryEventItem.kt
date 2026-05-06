package com.maeen.mahfilhub.data.model

data class HistoryEventItem(
    val id: Int,
    val title: String,
    val maulana: String,
    val location: String,
    val date: String,
    val attendees: Int,
    val rating: Float = 0f
)
