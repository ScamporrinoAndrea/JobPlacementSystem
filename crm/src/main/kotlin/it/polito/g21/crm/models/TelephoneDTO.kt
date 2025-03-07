package it.polito.g21.crm.models


import it.polito.g21.crm.entities.Telephone


data class TelephoneDTO(
    val id :  Long?,
    val number : String,
)


fun Telephone.toDTO() : TelephoneDTO =
    TelephoneDTO(this.id,this.number)