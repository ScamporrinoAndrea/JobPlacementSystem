package it.polito.g21.crm.repositories


import it.polito.g21.crm.entities.Customer
import it.polito.g21.crm.entities.Professional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProfessionalRepo : JpaRepository<Professional, Long> {

    fun findProfessionalByContactId(id : Long) : Professional?

}