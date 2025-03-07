package it.polito.g21.crm.models

import it.polito.g21.crm.entities.JobOffer

data class JobOfferDTO(
    val id: Long?,
    val description : String,
    val requiredSkills : String,
    val duration : Int,
    val status : String?,
    val profitMargin : Double,
    val customer: SpecificContactDTO?,
    val professionalId: Long?
){}

fun JobOffer.toDTO() : JobOfferDTO =
    JobOfferDTO(this.getId(), this.description, this.requiredSkills, this.duration, this.status.value,
        this.profitMargin, this.customer?.contact?.toFullDTO(), this.professional?.contact?.getId())