package it.polito.g21.crm.models

import it.polito.g21.crm.entities.Professional

data class ProfessionalDTO(
    val skills: String,
    val employmentState: String,
    val location: String,
    val dailyRate: Double,
    val linkedJobs: List<JobOfferDTO>?
){}

fun Professional.toDTO() : ProfessionalDTO =
    ProfessionalDTO(this.skills, this.employmentState.value, this.location, this.dailyRate, this.jobs.map{ it.toDTO() })

