package it.polito.g21.crm.models.topicdtos

import it.polito.g21.crm.entities.Contact
import java.time.LocalDate

data class ContactTopicDTO(
    val id: Long?,
    val category: String,
    val day: Int,
    val month: Int,
    val year: Int
){}

fun Contact.toTopicDTO() : ContactTopicDTO {
    val currentDate = LocalDate.now()
    return ContactTopicDTO(this.getId(), this.category.value, currentDate.dayOfMonth, currentDate.monthValue, currentDate.year)
}