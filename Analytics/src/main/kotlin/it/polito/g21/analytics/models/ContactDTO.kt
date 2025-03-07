package it.polito.g21.analytics.models

data class ContactDTO(
    val id: Long,
    val category: String,
    val day: Int,
    val month: Int,
    val year: Int
){}