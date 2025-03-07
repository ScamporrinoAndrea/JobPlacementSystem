package com.example.communicationmngr.dtos

data class NewEmailDTO(
    val recipient : String,
    val subject: String,
    val body: String
)