package it.polito.g21.analytics.models

data class JobDTO(
    val id: Long,
    val status: String,
    val day: Int,
    val month: Int,
    val year: Int
){}