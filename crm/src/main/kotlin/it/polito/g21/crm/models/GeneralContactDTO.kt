package it.polito.g21.crm.models

import it.polito.g21.crm.entities.Contact
import it.polito.g21.crm.utils.EmploymentState


//This contains general information for a contact
data class GeneralContactDTO(
    val id : Long?,
    val name : String,
    val surname : String,
    val category: String,
    val ssncode : String?,
    val professionalInfo: ProfessionalDTO?
)

fun Contact.toDTO() : GeneralContactDTO =
    GeneralContactDTO(this.getId(), this.name, this.surname, this.category.value, this.SSNCode, this.professional?.toDTO())

