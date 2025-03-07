package it.polito.g21.crm.services

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.g21.crm.entities.ActionOnJob
import it.polito.g21.crm.entities.JobOffer
import it.polito.g21.crm.exceptionhandler.*
import it.polito.g21.crm.models.ActionOnJobDTO
import it.polito.g21.crm.models.JobOfferDTO
import it.polito.g21.crm.models.JobStatusDTO
import it.polito.g21.crm.models.toDTO
import it.polito.g21.crm.models.topicdtos.toTopicDTO
import it.polito.g21.crm.repositories.ActionOnJobRepo
import it.polito.g21.crm.repositories.CustomerRepo
import it.polito.g21.crm.repositories.JobOfferRepo
import it.polito.g21.crm.repositories.ProfessionalRepo
import it.polito.g21.crm.utils.CategoryType
import it.polito.g21.crm.utils.EmploymentState
import it.polito.g21.crm.utils.JobStatus
import jakarta.transaction.Transactional
import org.springframework.data.domain.PageRequest
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
@Transactional
class JobOfferServiceImpl(private val jobOfferRepo: JobOfferRepo,
                          private val customerRepo: CustomerRepo,
                          private val professionalRepo: ProfessionalRepo,
                          private val actionOnJobRepo: ActionOnJobRepo,
                          private val objectMapper: ObjectMapper,
                          private val kafkaTemplate: KafkaTemplate<String, String>
) : JobOfferService {
    override fun createJobOffer(dto: JobOfferDTO, id: Long) : JobOfferDTO {
        val customer = customerRepo.findCustomerByContactId(id) ?: throw ContactNotFoundException("Customer having id: $id not found!")
        val job = JobOffer(dto.description, dto.requiredSkills, dto.duration, dto.profitMargin, customer)
        customer.addJob(job)
        customerRepo.save(customer)
        jobOfferRepo.save(job)
        val toSend = objectMapper.writeValueAsString(job.toTopicDTO("created"))
        kafkaTemplate.send("newJob", toSend)
        return job.toDTO()
    }

    override fun getAllJobOffers(): List<JobOfferDTO> {
        return jobOfferRepo.findAll().map { it.toDTO() }
    }

    override fun getJobOfferById(id: Long): JobOfferDTO? {
        return jobOfferRepo.findById(id).map { it.toDTO() }.orElse(null)?: throw JobOfferNotFoundException("Job Offer having id: $id not found!")
    }

    override fun getJobOffersByStatusAndId(
        contactId: Long?,
        category: String?,
        status: String?,
        pageNumber: Int?,
        limit: Int?
    ): List<JobOfferDTO> {
        val pageable = if(pageNumber != null && limit != null) {
            PageRequest.of(pageNumber-1, limit)
        } else {
            null
        }

        var newCat : CategoryType? = null
        if(contactId != null && category != null) {
            newCat = when (category) {
                "customer" -> CategoryType.CUSTOMER
                "professional" -> CategoryType.PROFESSIONAL
                else -> throw InvalidCategoryException("category parameter should be either 'customer' or 'professional'.")
            }
        }
        else if((contactId == null) xor (category == null)){
            throw InvalidFilterParamsException("contactId and category parameters must be both present or absent.")
        }

        val newStatus = when(status){
            "open" -> listOf(JobStatus.CREATED, JobStatus.SELECTION_PHASE,  JobStatus.CANDIDATE_PROPOSAL)
            "aborted" -> listOf(JobStatus.ABORTED)
            "accepted" -> listOf(JobStatus.CONSOLIDATED, JobStatus.DONE)
            "created" -> listOf(JobStatus.CREATED)
            "selection_phase" -> listOf(JobStatus.SELECTION_PHASE)
            "candidate_proposal" -> listOf(JobStatus.CANDIDATE_PROPOSAL)
            "consolidated" -> listOf(JobStatus.CONSOLIDATED)
            "done" -> listOf(JobStatus.DONE)
            null -> null
            else -> throw InvalidStateException("status parameter provided is not valid.")
        }

        //CASO IN CUI PROFESSIONAL FILTRA PER JOB IN CUI NON è STATO ANCORA ASSEGNATO
        if (newStatus != null) {
            if(newCat == CategoryType.PROFESSIONAL &&
                (newStatus.contains(JobStatus.CREATED) ||
                        newStatus.contains(JobStatus.SELECTION_PHASE) ||
                        newStatus.contains(JobStatus.CANDIDATE_PROPOSAL)))
                throw InvalidFilterParamsException("Searching for open Job Offers linked to a professional is impossible!")
        }

        return jobOfferRepo.findJobOffersByStatusAndContactId(contactId, newStatus, newCat, pageable).map{it.toDTO()}
        }

    override fun updateJobOfferStatus(id: Long, dto: JobStatusDTO): JobOfferDTO {
        val jobOffer = jobOfferRepo.findById(id).orElse(null) ?:
            throw JobOfferNotFoundException("Job Offer having id: $id not found!")

        val newStatus = when(dto.status){
            "selection_phase" -> JobStatus.SELECTION_PHASE
            "candidate_proposal" -> JobStatus.CANDIDATE_PROPOSAL
            "consolidated" -> JobStatus.CONSOLIDATED
            "done" -> JobStatus.DONE
            "aborted" -> JobStatus.ABORTED
            else -> throw InvalidStateException("New State provided is not valid.")
        }

        when(jobOffer.status){
            JobStatus.CREATED -> {
                if(newStatus != JobStatus.ABORTED && newStatus != JobStatus.SELECTION_PHASE)
                    throw InvalidJobOfferFlowException("Job Offer can't move from status ${jobOffer.status.value} to ${newStatus.value}.")
            }
            JobStatus.SELECTION_PHASE -> {
                if(newStatus != JobStatus.ABORTED && newStatus != JobStatus.CANDIDATE_PROPOSAL)
                    throw InvalidJobOfferFlowException("Job Offer can't move from status ${jobOffer.status.value} to ${newStatus.value}.")
            }
            JobStatus.CANDIDATE_PROPOSAL -> {
                if(newStatus != JobStatus.ABORTED && newStatus != JobStatus.CONSOLIDATED && newStatus != JobStatus.SELECTION_PHASE)
                    throw InvalidJobOfferFlowException("Job Offer can't move from status ${jobOffer.status.value} to ${newStatus.value}.")
            }
            JobStatus.CONSOLIDATED -> {
                if(newStatus != JobStatus.ABORTED && newStatus != JobStatus.SELECTION_PHASE && newStatus != JobStatus.DONE)
                    throw InvalidJobOfferFlowException("Job Offer can't move from status ${jobOffer.status.value} to ${newStatus.value}.")
            }
            else ->  throw InvalidJobOfferFlowException("Job Offer can't change its state anymore.")
        }


        // todo:Controlli nel caso della update dello stato e nella delete del professional
        if(jobOffer.status == JobStatus.CANDIDATE_PROPOSAL && newStatus == JobStatus.CONSOLIDATED){
            if(dto.professionalId == null)
                throw InvalidJobOfferFlowException("A professional must be linked in order to move to 'consolidated' state")
            val professional = professionalRepo.findProfessionalByContactId(dto.professionalId) ?: throw ContactNotFoundException("Professional not Found!")
            if(professional.employmentState != EmploymentState.AVAILABLE) {
                jobOffer.status = JobStatus.SELECTION_PHASE
                jobOfferRepo.save(jobOffer)
                val action = ActionOnJob(
                    state = JobStatus.SELECTION_PHASE,
                    date = LocalDateTime.now(),
                    note = "The professional selected was not available anymore",
                    job = jobOffer,
                    professional = null
                )

                actionOnJobRepo.save(action)
                val toSend = objectMapper.writeValueAsString(jobOffer.toTopicDTO("failed"))
                kafkaTemplate.send("newJob", toSend)
                return jobOffer.toDTO()
                //throw InvalidJobOfferFlowException("A professional must be available in order to move to 'consolidated' state")
            }
            else{
                professional.employmentState = EmploymentState.EMPLOYED
                professional.addJob(jobOffer)
                professionalRepo.save(professional)
                jobOffer.professional = professional
            }
        }

        if(jobOffer.status == JobStatus.CONSOLIDATED &&
            (newStatus == JobStatus.SELECTION_PHASE || newStatus == JobStatus.ABORTED || newStatus == JobStatus.DONE)){
            val professional = jobOffer.professional
            if (professional != null) {
                professional.employmentState = EmploymentState.AVAILABLE

                professionalRepo.save(professional)
                if (newStatus == JobStatus.SELECTION_PHASE) {
                    jobOffer.professional = null
                }
            }
        }


        jobOffer.apply {
            status = newStatus
        }

        val action = ActionOnJob(
            state = newStatus,
            date = LocalDateTime.now(),
            note = dto.note,
            job = jobOffer,
            professional = jobOffer.professional
        )

        actionOnJobRepo.save(action)

        jobOfferRepo.save(jobOffer)
        if(jobOffer.status == JobStatus.CONSOLIDATED){
            val toSend = objectMapper.writeValueAsString(jobOffer.toTopicDTO("consolidated"))
            kafkaTemplate.send("newJob", toSend)
        }
        else if(jobOffer.status == JobStatus.ABORTED){
            val toSend = objectMapper.writeValueAsString(jobOffer.toTopicDTO("aborted"))
            kafkaTemplate.send("newJob", toSend)
        }
        return jobOffer.toDTO()

    }

    override fun computeJobOfferValue(id: Long): String {
        val jobOffer = jobOfferRepo.findById(id).orElse(null) ?: throw JobOfferNotFoundException("Job Offer having id: $id not found!")


        val professional = jobOffer.professional
        if (professional != null) {
            return "%.2f".format(professional.dailyRate * jobOffer.profitMargin * jobOffer.duration)
        } else {
            throw InvalidJobOfferFlowException("Job Offer must be in an accepted state in order to compute the value.")
        }
    }

    override fun getJobHistory(id: Long): List<ActionOnJobDTO> {
        jobOfferRepo.findById(id).orElse(null) ?: throw JobOfferNotFoundException("Job Offer having id: $id not found!")
        return actionOnJobRepo.findByJobId(id).map { it.toDTO() }
    }

    override fun getCustomerHistory(id: Long): List<ActionOnJobDTO> {
        customerRepo.findCustomerByContactId(id) ?: throw ContactNotFoundException("Customer having id: $id not found!")
        return actionOnJobRepo.findByCustomerId(id).map { it.toDTO() }
    }

    override fun getProfessionalHistory(id: Long): List<ActionOnJobDTO> {
        professionalRepo.findProfessionalByContactId(id) ?: throw ContactNotFoundException("Professional having id: $id not found!")
        return actionOnJobRepo.findByProfessionalId(id).map { it.toDTO() }
    }

    override fun updateJobOffer(id: Long, dto: JobOfferDTO): JobOfferDTO {
        val job = jobOfferRepo.findById(id).orElse(null) ?: throw JobOfferNotFoundException("Job Offer having id: $id not found!")
        job.apply {
            description = dto.description
            requiredSkills = dto.requiredSkills
            duration = dto.duration
            profitMargin = dto.profitMargin
        }
        jobOfferRepo.save(job)
        return job.toDTO()
    }


}