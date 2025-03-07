package it.polito.g21.crm.models

import it.polito.g21.crm.entities.ActionOnJob
import it.polito.g21.crm.entities.Email
import it.polito.g21.crm.entities.JobOffer
import it.polito.g21.crm.entities.Professional
import it.polito.g21.crm.utils.JobStatus
import java.time.LocalDateTime

data class ActionOnJobDTO(
    val state : JobStatus,
    val date : LocalDateTime,
    val note: String?,
    val jobOfferId: Long?,
    val professionalId: Long?
)

fun ActionOnJob.toDTO() : ActionOnJobDTO =
    ActionOnJobDTO(this.state, this.date, this.note, this.job?.getId(), this.professional?.getId())