package it.polito.g21.crm.controllers

import it.polito.g21.crm.models.GeneralContactDTO
import it.polito.g21.crm.models.NoteDTO
import it.polito.g21.crm.models.SpecificContactDTO
import it.polito.g21.crm.services.ContactService
import it.polito.g21.crm.utils.CategoryType
import it.polito.g21.crm.utils.LoggerConfig
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.logging.Logger

@RestController
@RequestMapping("/API/customers/")
class CustomerController(val contactService : ContactService, val logger: LoggerConfig) {
    @PostMapping("", "/")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('manager') or hasRole('operator')")
    fun createCustomer( @RequestBody dto : GeneralContactDTO) : GeneralContactDTO{
        val res = contactService.createContact(dto, CategoryType.CUSTOMER)
        logger.info("Customer created successfully")
        return res
    }

    @GetMapping("","/")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('manager') or hasRole('guest')  or hasRole('operator')")
    fun getAllCustomer(@Valid @RequestParam("pageNumber") @Min(1) pageNumber: Int?,
                       @Valid @RequestParam("limit") @Min(0) limit: Int?,
                       @RequestParam("name") name: String?,
                       @RequestParam("surname") surname: String?,
                       @RequestParam("ssnCode") ssnCode: String?,
                       @RequestParam("email") email: String?,
                       @RequestParam("city") city: String?,
                       @RequestParam("country") country: String?,
                       @RequestParam("street") street: String?,
                       @RequestParam("telephone") telephone: String?): Map<String,List<SpecificContactDTO>>{
        val res = contactService.getAllContacts(pageNumber, limit, name, surname, "customer", ssnCode, email, city, country, street, telephone, null, null, null, null)
        return mapOf("customers" to res)
    }

    @GetMapping("{customerId}","{customerId}/")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('manager') or hasRole('guest')  or hasRole('operator')")
    fun getCustomerById(@PathVariable("customerId") id: Long): SpecificContactDTO?{
        return contactService.getContactById(id, CategoryType.CUSTOMER)
    }

    @PutMapping("{customerId}/","{customerId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('manager') or hasRole('operator')")
    fun updateCustomer(@PathVariable("customerId") id: Long, @RequestBody dto: GeneralContactDTO): SpecificContactDTO? {
        val res = contactService.updateContact(id, CategoryType.CUSTOMER, dto)
        logger.info("Customer updated successfully")
        return res
    }

    @DeleteMapping("{customerId}/","{customerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('manager') or hasRole('operator')")
    fun deleteCustomer(@PathVariable("customerId") id: Long){
        contactService.deleteContact(id, CategoryType.CUSTOMER)
        logger.info("Customer deleted successfully")
    }

    @PostMapping("{customerId}/notes/","{customerId}/notes")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('manager') or hasRole('operator')")
    fun addCustomerNotes(@PathVariable("customerId") id: Long, @RequestBody dto: NoteDTO){
        contactService.addNotes(id, CategoryType.CUSTOMER, dto)
        logger.info("Customer's note added successfully")
    }

    @GetMapping("{customerId}/notes/","{customerId}/notes")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('manager') or hasRole('guest')  or hasRole('operator')")
    fun getCustomerNotesById(@PathVariable("customerId") id: Long) : Map<String, List<NoteDTO>>{
        return mapOf("notes" to contactService.getNotesById(id, CategoryType.CUSTOMER))
    }
}