package it.polito.g21.crm.models

import it.polito.g21.crm.entities.Email
import it.polito.g21.crm.entities.NewEmail

data class NewEmailDTO(
    val recipient : String,
    val subject: String,
    val body: String
)

fun NewEmail.toDTO() : NewEmailDTO =
    NewEmailDTO(this.recipient,this.subject, this.body)