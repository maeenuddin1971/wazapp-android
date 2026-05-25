package com.maeen.mahfilhub.data.model

/**
 * Request body for POST /mahfil/list.
 * Supports filtering by maulana name, date range, and pagination.
 */
data class MahfilListRequest(
    val maolanaName: String = "",
    val from: String? = null,
    val to: String? = null,
    val page: Int = 0,
    val size: Int = 20
)
