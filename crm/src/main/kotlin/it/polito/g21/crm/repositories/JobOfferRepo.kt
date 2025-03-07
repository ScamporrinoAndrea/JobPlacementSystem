package it.polito.g21.crm.repositories


import it.polito.g21.crm.entities.JobOffer
import it.polito.g21.crm.models.JobOfferDTO
import it.polito.g21.crm.utils.CategoryType
import it.polito.g21.crm.utils.JobStatus
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface JobOfferRepo : JpaRepository<JobOffer, Long> {
    @Query(
        " SELECT j FROM JobOffer j " +
        " left JOIN j.customer.contact cu " +
        " left JOIN j.professional.contact p" +
        " WHERE (:statusList IS NULL OR j.status IN :statusList) " +
        "AND (:id IS NULL OR ((p.id = :id and p.category = :category)" +
                "or (cu.id = :id and cu.category = :category))) "
    )
    fun findJobOffersByStatusAndContactId(id : Long?, statusList: List<JobStatus>?, category: CategoryType?, pageable: Pageable?) : List<JobOffer>
}