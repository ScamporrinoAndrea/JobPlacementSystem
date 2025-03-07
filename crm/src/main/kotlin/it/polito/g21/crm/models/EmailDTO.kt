package it.polito.g21.crm.models


import it.polito.g21.crm.entities.Email

data class EmailDTO(
    val id : Long?,
    val mail : String,
)

fun Email.toDTO() : EmailDTO =
    EmailDTO(this.id,this.mail)