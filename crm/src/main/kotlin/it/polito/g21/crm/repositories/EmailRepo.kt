package it.polito.g21.crm.repositories

import it.polito.g21.crm.entities.Email
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EmailRepo : JpaRepository<Email, Long> {
    fun findByMail(mail: String): Email?
}