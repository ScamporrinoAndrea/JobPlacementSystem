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
@RequestMapping("/API/professionals/")
class ProfessionalController(val contactService : ContactService, val logger: LoggerConfig) {
    @PostMapping("", "/")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('manager') or hasRole('guest')  or hasRole('operator')")
    fun createProfessional( @RequestBody dto : GeneralContactDTO) : GeneralContactDTO{
        val res = contactService.createContact(dto, CategoryType.PROFESSIONAL)
        logger.info("Professional created successfully")
        return res
    }

    @GetMapping("","/")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('manager') or hasRole('guest')  or hasRole('operator')")
    fun getAllProfessional(
        @Valid @RequestParam("pageNumber") @Min(1) pageNumber: Int?,
        @Valid @RequestParam("limit") @Min(0) limit: Int?,
        @RequestParam("name") name: String?,
        @RequestParam("surname") surname: String?,
        @RequestParam("ssnCode") ssnCode: String?,
        @RequestParam("email") email: String?,
        @RequestParam("city") city: String?,
        @RequestParam("country") country: String?,
        @RequestParam("street") street: String?,
        @RequestParam("telephone") telephone: String?,
        @RequestParam("skills") skills: String?,
        @RequestParam("empState") empState: String?,
        @RequestParam("location") location: String?,
        @RequestParam("dailyRate") dailyRate: Double?
    ): Map<String,List<SpecificContactDTO>>{
        val res = contactService.getAllContacts(pageNumber, limit, name, surname, "professional", ssnCode, email, city, country, street, telephone, skills, empState, location, dailyRate)
        return mapOf("professionals" to res)
    }

    @GetMapping("{professionalId}","{professionalId}/")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('manager') or hasRole('guest') or hasRole('operator')")
    fun getProfessionalById(@PathVariable("professionalId") id: Long): SpecificContactDTO?{
        return contactService.getContactById(id, CategoryType.PROFESSIONAL)
    }

    @PutMapping("{professionalId}/","{professionalId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('manager') or hasRole('guest')  or hasRole('operator')")
    fun updateProfessional(@PathVariable("professionalId") id: Long, @RequestBody dto: GeneralContactDTO): SpecificContactDTO? {
        val res = contactService.updateContact(id, CategoryType.PROFESSIONAL, dto)
        logger.info("Professional updated successfully")
        return res
    }

    @DeleteMapping("{professionalId}/","{professionalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('manager') or hasRole('guest')  or hasRole('operator')")
    fun deleteProfessional(@PathVariable("professionalId") id: Long){
        contactService.deleteContact(id, CategoryType.PROFESSIONAL)
        logger.info("Professional deleted successfully")
    }

    @PostMapping("{professionalId}/notes/","{professionalId}/notes")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('manager') or hasRole('guest')  or hasRole('operator')")
    fun addProfessionalNotes(@PathVariable("professionalId") id: Long, @RequestBody dto: NoteDTO){
        contactService.addNotes(id, CategoryType.PROFESSIONAL, dto)
        logger.info("Professional's note added successfully")
    }

    @GetMapping("{professionalId}/notes/","{professionalId}/notes")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('manager') or hasRole('guest') or hasRole('operator')")
    fun getProfessionalNotesById(@PathVariable("professionalId") id: Long) : Map<String, List<NoteDTO>>{
        return mapOf("notes" to contactService.getNotesById(id, CategoryType.PROFESSIONAL))
    }
}