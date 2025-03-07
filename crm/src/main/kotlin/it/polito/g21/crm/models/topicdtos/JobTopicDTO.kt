package it.polito.g21.crm.models.topicdtos

import it.polito.g21.crm.entities.JobOffer
import java.time.LocalDate

data class JobTopicDTO(
    val id: Long?,
    val status: String,
    val day: Int,
    val month: Int,
    val year: Int
){}

fun JobOffer.toTopicDTO(state: String) : JobTopicDTO{
    val currentDate = LocalDate.now()
    return JobTopicDTO(this.getId(), state, currentDate.dayOfMonth, currentDate.monthValue, currentDate.year)
}