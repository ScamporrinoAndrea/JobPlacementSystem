package it.polito.g21.crm.repositories

import it.polito.g21.crm.entities.Telephone
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface TelephoneRepo : JpaRepository<Telephone, Long> {
    @Query("SELECT t FROM Telephone t WHERE " +
            "t.number = :number ")
    fun findByTelephoneDetails(number: String): Telephone?
}