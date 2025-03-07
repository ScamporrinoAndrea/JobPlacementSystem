package it.polito.g21.crm.models

import it.polito.g21.crm.entities.Note
import java.time.LocalDate

class NoteDTO(
    val note: String,
    val date: LocalDate?
){}

fun Note.toDTO() : NoteDTO =
    NoteDTO(this.message, this.date)