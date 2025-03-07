package it.polito.g21.crm.controllers

import it.polito.g21.crm.entities.ActionOnJob
import it.polito.g21.crm.entities.JobOffer
import it.polito.g21.crm.models.ActionOnJobDTO
import it.polito.g21.crm.models.JobOfferDTO
import it.polito.g21.crm.models.JobStatusDTO
import it.polito.g21.crm.services.JobOfferService
import it.polito.g21.crm.utils.LoggerConfig
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/API/joboffers/")
class JobOfferController(val jobOfferService : JobOfferService, private val logger: LoggerConfig) {
    //val logger: Logger = Logger.getLogger("JobOfferController")

    @PostMapping("", "/")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('manager') or hasRole('operator')")
    fun createJobOffer(@RequestParam("customer") id: Long, @RequestBody dto : JobOfferDTO) : JobOfferDTO{
        val res = jobOfferService.createJobOffer(dto, id)
        logger.info("Job Offer created successfully")
        return res
    }


    @GetMapping("{jobOfferId}", "/{jobOfferId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('manager') or hasRole('guest')  or hasRole('operator')")
    fun getJobOfferById(@PathVariable("jobOfferId") id: Long) : JobOfferDTO?{
        return jobOfferService.getJobOfferById(id)
    }

    @GetMapping("", "/")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('manager') or hasRole('guest')  or hasRole('operator')")
    fun getJobOffersByStatusAndId(@Valid @RequestParam("pageNumber") @Min(1) pageNumber: Int?,
                                  @Valid @RequestParam("limit") @Min(0) limit: Int?,
                                  @RequestParam("contactId") contactId : Long?,
                                  @RequestParam("status") status : String?,
                                  @RequestParam("category") category : String?)
    :Map<String, List<JobOfferDTO>>{
        return mapOf("jobOffers" to jobOfferService.getJobOffersByStatusAndId(contactId, category, status, pageNumber, limit))
    }

    @PostMapping("{jobOfferId}", "/{jobOfferId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('manager') or hasRole('guest')")
    fun updateJobOfferStatus(@PathVariable("jobOfferId") id: Long,
                             @RequestBody dto: JobStatusDTO): JobOfferDTO{
        val res = jobOfferService.updateJobOfferStatus(id, dto)
        logger.info("Job Offer status updated successfully")
        return res
    }

    @GetMapping("{jobOfferId}/value/", "{jobOfferId}/value")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('manager') or hasRole('guest')  or hasRole('operator')")
    fun getJobOfferValue(@PathVariable("jobOfferId") id: Long): Map<String, String>{
        return mapOf("job offer value" to jobOfferService.computeJobOfferValue(id))
    }

    @GetMapping("history/joboffer/{jobOfferId}/", "history/joboffer/{jobOfferId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('manager') or hasRole('guest')  or hasRole('operator')")
    fun getJobOfferHistory(@PathVariable("jobOfferId") id: Long) : Map<String, List<ActionOnJobDTO>>{
        return mapOf("job offer history" to jobOfferService.getJobHistory(id))
    }

    @GetMapping("history/customer/{customerId}/", "history/customer/{customerId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('manager') or hasRole('guest')  or hasRole('operator')")
    fun getCustomerHistory(@PathVariable("customerId") id: Long) : Map<String, List<ActionOnJobDTO>>{
        return mapOf("job offer history" to jobOfferService.getCustomerHistory(id))
    }

    @GetMapping("history/professional/{professionalId}/", "history/professional/{professionalId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('manager') or hasRole('guest')  or hasRole('operator')")
    fun getProfessionalHistory(@PathVariable("professionalId") id: Long) : Map<String, List<ActionOnJobDTO>>{
        return mapOf("job offer history" to jobOfferService.getProfessionalHistory(id))
    }

    @PutMapping("{jobOfferId}/", "{jobOfferId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('manager') or hasRole('guest')  or hasRole('operator')")
    fun updateJobOffer(@PathVariable("jobOfferId") id: Long, @RequestBody dto: JobOfferDTO) : JobOfferDTO{
        val res = jobOfferService.updateJobOffer(id, dto)
        logger.info("Job Offer updated successfully")
        return res
    }

}