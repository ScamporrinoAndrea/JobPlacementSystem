package it.polito.g21.crm.services

import it.polito.g21.crm.entities.Professional
import it.polito.g21.crm.models.ActionOnJobDTO
import it.polito.g21.crm.models.JobOfferDTO
import it.polito.g21.crm.models.JobStatusDTO
import it.polito.g21.crm.utils.JobStatus

interface JobOfferService {
    fun createJobOffer(dto : JobOfferDTO, id: Long) : JobOfferDTO

    fun getAllJobOffers() : List<JobOfferDTO>


    fun getJobOfferById( id : Long) : JobOfferDTO?

    fun getJobOffersByStatusAndId(
        contactId: Long?,
        category: String?,
        status: String?,
        pageNumber: Int?,
        limit: Int?
    ): List<JobOfferDTO>

    fun updateJobOfferStatus(
        id: Long,
        dto: JobStatusDTO
    ): JobOfferDTO

    fun computeJobOfferValue(id : Long) : String

    fun getJobHistory(id : Long) : List<ActionOnJobDTO>

    fun getCustomerHistory(id : Long) : List<ActionOnJobDTO>

    fun getProfessionalHistory(id : Long) : List<ActionOnJobDTO>

    fun updateJobOffer(id: Long, dto: JobOfferDTO) : JobOfferDTO
}