package it.polito.g21.crm.models

import it.polito.g21.crm.entities.Address


data class AddressDTO(
    val id : Long?,
    val address: String
)
fun Address.toDTO() : AddressDTO =
    AddressDTO(this.id,this.toString())