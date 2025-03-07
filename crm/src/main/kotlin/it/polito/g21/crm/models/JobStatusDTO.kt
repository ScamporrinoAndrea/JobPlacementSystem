package it.polito.g21.crm.models


import it.polito.g21.crm.entities.JobOffer

data class JobStatusDTO(
    val status : String,
    val note : String?,
    val professionalId: Long?
)