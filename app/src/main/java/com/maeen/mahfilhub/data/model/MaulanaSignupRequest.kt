package com.maeen.mahfilhub.data.model

import com.google.gson.annotations.SerializedName

/**
 * Request body for the Maulana creation endpoint: POST /maolana/create
 * Requires JWT Bearer token in the Authorization header.
 *
 * All fields match the backend API contract exactly.
 */
data class MaulanaSignupRequest(
    @SerializedName("fullName")
    val fullName: String,

    @SerializedName("contactNumber")
    val contactNumber: String,

    @SerializedName("whatsappNumber")
    val whatsappNumber: String? = null,

    @SerializedName("email")
    val email: String,

    @SerializedName("nid")
    val nid: String? = null,

    @SerializedName("address")
    val address: String,

    // ── Geographic IDs (Division → District → Upazila → Union) ──────
    @SerializedName("wzDivisionId")
    val wzDivisionId: String? = null,

    @SerializedName("wzDistrictId")
    val wzDistrictId: String? = null,

    @SerializedName("wzUpazilaId")
    val wzUpazilaId: String? = null,

    @SerializedName("wzUnionId")
    val wzUnionId: String? = null,

    // ── Education & Qualification ───────────────────────────────────
    @SerializedName("highestIslamicDegree")
    val highestIslamicDegree: String,

    @SerializedName("degreeTitleBengali")
    val degreeTitleBengali: String? = null,

    @SerializedName("madrasaName")
    val madrasaName: String? = null,

    @SerializedName("graduationYear")
    val graduationYear: Int? = null,

    @SerializedName("specializationField")
    val specializationField: String,

    // ── Current Position ────────────────────────────────────────────
    @SerializedName("currentMosqueName")
    val currentMosqueName: String? = null,

    @SerializedName("currentMosqueAddress")
    val currentMosqueAddress: String? = null,

    @SerializedName("designation")
    val designation: String,

    @SerializedName("yearsOfExperience")
    val yearsOfExperience: Int,

    // ── Availability Flags ──────────────────────────────────────────
    @SerializedName("availableForNikah")
    val availableForNikah: Boolean = false,

    @SerializedName("availableForCounseling")
    val availableForCounseling: Boolean = false,

    @SerializedName("availableForFatwa")
    val availableForFatwa: Boolean = false,

    @SerializedName("availableForLectures")
    val availableForLectures: Boolean = true,

    @SerializedName("availableForOnlineClasses")
    val availableForOnlineClasses: Boolean = false,

    // ── Social Links ────────────────────────────────────────────────
    @SerializedName("youtubeChannel")
    val youtubeChannel: String? = null,

    @SerializedName("facebookPage")
    val facebookPage: String? = null,

    @SerializedName("website")
    val website: String? = null,

    // ── Status ──────────────────────────────────────────────────────
    @SerializedName("isVerified")
    val isVerified: Boolean = false,

    @SerializedName("isActive")
    val isActive: Boolean = true,

    @SerializedName("profilePhotoUrl")
    val profilePhotoUrl: String? = null
)
