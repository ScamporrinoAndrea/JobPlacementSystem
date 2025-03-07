package it.polito.g21.crm.repositories

import it.polito.g21.crm.entities.Address
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface AddressRepo : JpaRepository<Address, Long> {

    @Query("SELECT a FROM Address a WHERE " +
            "a.streetNumber = :streetNumber " +
            "AND a.streetName = :streetName " +
            "AND a.city = :city " +
            "AND a.country = :country")
    fun findByAddressDetails(streetName: String, streetNumber: String, city: String, country: String): Address?
}