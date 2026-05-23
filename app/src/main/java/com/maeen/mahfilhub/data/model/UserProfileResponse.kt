package com.maeen.mahfilhub.data.model

import com.google.gson.annotations.SerializedName

/**
 * Base profile response from GET /user/profile.
 * Returned for all user roles. Extended by [MaolanaProfileResponse]
 * for users with ISLAMIC_CLERIC role.
 */
open class UserProfileResponse(
    val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val contactNumber: String = "",
    val profilePhotoUrl: String? = null,
    val userId: String = "",
    val role: String = "",
    @SerializedName("active")
    val isActive: Boolean = false,
    @SerializedName("verified")
    val isVerified: Boolean = false,

    // ── Location (resolved names) ────────────────────────────────────
    val divisionName: String? = null,
    val districtName: String? = null,
    val upazilaName: String? = null,
    val unionName: String? = null,

    // ── Maolana-specific fields (present when role = ISLAMIC_CLERIC) ─
    val nid: String? = null,
    val whatsappNumber: String? = null,
    val address: String? = null,
    val highestIslamicDegree: String? = null,
    val degreeTitleBengali: String? = null,
    val madrasaName: String? = null,
    val graduationYear: Int? = null,
    val specializationField: String? = null,
    val currentMosqueName: String? = null,
    val currentMosqueAddress: String? = null,
    val designation: String? = null,
    val yearsOfExperience: Int? = null,
    val availableForNikah: Boolean? = null,
    val availableForCounseling: Boolean? = null,
    val availableForFatwa: Boolean? = null,
    val availableForLectures: Boolean? = null,
    val availableForOnlineClasses: Boolean? = null,
    val youtubeChannel: String? = null,
    val facebookPage: String? = null,
    val website: String? = null
) {
    /** Whether this profile belongs to an Islamic cleric. */
    val isMaolana: Boolean get() = role == "ISLAMIC_CLERIC"

    /** Display name initial for avatar. */
    val initial: String get() = fullName.firstOrNull()?.uppercase() ?: "?"

    /** Formatted location string. */
    val locationDisplay: String
        get() = listOfNotNull(upazilaName, districtName, divisionName)
            .joinToString(", ")
            .ifBlank { "Not set" }
}
