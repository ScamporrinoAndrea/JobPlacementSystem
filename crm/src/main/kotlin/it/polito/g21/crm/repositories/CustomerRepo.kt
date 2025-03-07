package it.polito.g21.crm.repositories

import it.polito.g21.crm.entities.Customer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomerRepo : JpaRepository<Customer, Long> {
    fun findCustomerByContactId(id : Long) : Customer?
}