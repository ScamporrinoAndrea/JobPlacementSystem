package it.polito.g21.crm.models

import it.polito.g21.crm.entities.Contact

data class SpecificContactDTO(
        val id : Long?,
        val name : String,
        val surname : String,
        val category: String,
        val ssncode : String?,
        val mailList : List<EmailDTO>,
        val addressList : List<AddressDTO>,
        val telephoneList : List<TelephoneDTO>,
        val professionalInfo : ProfessionalDTO?
)

fun Contact.toFullDTO() : SpecificContactDTO =
    SpecificContactDTO(this.getId(), this.name, this.surname, this.category.value, this.SSNCode, this.emails.map{it.toDTO()},
            this.addresses.map{it.toDTO()}, this.telephones.map{it.toDTO()}, this.professional?.toDTO() )

