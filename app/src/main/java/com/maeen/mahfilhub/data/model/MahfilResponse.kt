package com.maeen.mahfilhub.data.model

/**
 * Single Mahfil item returned from GET/POST mahfil endpoints.
 * Matches backend's WzMahfilResponseDTO.
 */
data class MahfilResponse(
    val id: String = "",
    val title: String = "",
    val phone: String = "",
    val description: String = "",
    val eventDateTime: String? = null,
    val organizerName: String = "",
    val organizerPhone: String = "",
    val mainSpeakerName: String = "",
    val divisionName: String = "",
    val districtName: String = "",
    val upazilaName: String = "",
    val unionName: String = ""
) {
    /** Formatted location from division/district/upazila. */
    val locationDisplay: String
        get() = listOfNotNull(
            upazilaName.ifBlank { null },
            districtName.ifBlank { null },
            divisionName.ifBlank { null }
        ).joinToString(", ").ifBlank { "Location TBA" }

    /** Convert to the existing EventItem model used by UI. */
    fun toEventItem(): EventItem {
        // Parse date/time from eventDateTime (ISO format: 2024-01-15T18:00:00)
        val datePart = eventDateTime?.substringBefore("T") ?: "TBA"
        val timePart = eventDateTime?.substringAfter("T")?.substringBeforeLast(":") ?: "TBA"

        return EventItem(
            id = id.hashCode(),    // EventItem uses Int IDs
            title = title.ifBlank { "Untitled Mahfil" },
            maulana = mainSpeakerName.ifBlank { "Speaker TBA" },
            location = locationDisplay,
            date = datePart,
            time = timePart,
            isLive = false,
            isFeatured = false,
            attendees = 0,
            category = "All"
        )
    }
}

/**
 * Paginated response wrapper matching Spring's Page response.
 */
data class MahfilPageResponse(
    val content: List<MahfilResponse> = emptyList(),
    val totalElements: Long = 0,
    val totalPages: Int = 0,
    val number: Int = 0,
    val size: Int = 20,
    val first: Boolean = true,
    val last: Boolean = true,
    val empty: Boolean = true
)
