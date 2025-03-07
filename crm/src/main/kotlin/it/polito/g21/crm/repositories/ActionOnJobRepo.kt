package it.polito.g21.crm.repositories

import it.polito.g21.crm.entities.ActionOnJob
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ActionOnJobRepo: JpaRepository<ActionOnJob, Long> {

    fun findByJobId(jobId: Long): List<ActionOnJob>

    @Query("SELECT a FROM ActionOnJob a " +
            "left JOIN a.job.customer.contact c " +
            "WHERE c.id = :customerId")
    fun findByCustomerId(customerId: Long): List<ActionOnJob>

    @Query("SELECT a FROM ActionOnJob a " +
            "left JOIN a.professional.contact c " +
            "WHERE c.id = :professionalId")
    fun findByProfessionalId(professionalId: Long): List<ActionOnJob>
}

