package com.example.communicationmngr.dtos

import java.time.LocalDate

data class MessageDTO(
    val sender: String,
    val date : LocalDate?,
    val subject : String?,
    val body : String?,
    val channel : String,
    val state : String?,
    val priority : Int?,

    )