package it.polito.g21.crm.controllers


import it.polito.g21.crm.models.*
import it.polito.g21.crm.services.ContactService
import it.polito.g21.crm.utils.CategoryType
import it.polito.g21.crm.utils.LoggerConfig
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.web.bind.annotation.*
import java.util.logging.Logger

@RestController
@RequestMapping("/API/contacts")
@EnableWebSecurity
class ContactController(val contactService : ContactService, private val logger: LoggerConfig) {

    //Contacts
    @GetMapping("","/")
    @PreAuthorize("hasRole('manager') or hasRole('guest')  or hasRole('operator')")
    @ResponseStatus(HttpStatus.OK)
    fun getAllContacts(@Valid @RequestParam("pageNumber") @Min(1) pageNumber: Int?,
                       @Valid @RequestParam("limit") @Min(0) limit: Int?,
                       @RequestParam("name") name: String?,
                       @RequestParam("surname") surname: String?,
                       @RequestParam("category") category: String?,
                       @RequestParam("ssnCode") ssnCode: String?,
                       @RequestParam("email") email: String?,
                       @RequestParam("city") city: String?,
                       @RequestParam("country") country: String?,
                       @RequestParam("street") street: String?,
                       @RequestParam("telephone") telephone: String?
        ) : Map<String,List<SpecificContactDTO>>{
        return mapOf("contacts" to contactService.getAllContacts(pageNumber, limit, name, surname, category, ssnCode, email, city, country, street, telephone, null, null, null, null))
    }

    @PostMapping("", "/")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('manager')  or hasRole('operator')")
    fun createContact( @RequestBody dto : GeneralContactDTO) : GeneralContactDTO{
        val res = contactService.createContact(dto, null)
        logger.info("Contact created successfully")
        return res
    }

    @GetMapping("{contactId}", "{contactId}/")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('manager') or hasRole('guest')  or hasRole('operator')")
    fun getContactById(@PathVariable("contactId") id: Long) : SpecificContactDTO? {
        return contactService.getContactById(id, null)
    }


    @PutMapping("/{contactId}/category","/{contactId}/category/")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('manager') or hasRole('guest')  or hasRole('operator')")
    fun updateCategory(@PathVariable("contactId") id: Long, @RequestParam("value") category : String, @RequestBody dto: ProfessionalDTO?) : GeneralContactDTO?{
        val res = contactService.updateCategory(id,category, dto)
        logger.info("Category updated successfully")
        return res
    }

    //Mail
    @PostMapping("{contactId}/email", "{contactId}/email/")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('manager') or hasRole('guest')  or hasRole('operator')")
    fun addContactEmail(@PathVariable("contactId") id: Long, @RequestBody dto : EmailDTO) {
        contactService.addEmail(id, dto)
        logger.info("Email added to the contact successfully")
    }


    @DeleteMapping("/{contactId}/email/{emailId}","/{contactId}/email/{emailId}/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('manager') or hasRole('guest')  or hasRole('operator')")
    fun deleteContactEmail(@PathVariable("contactId") contactId : Long, @PathVariable("emailId") emailId : Long){
        val res = contactService.deleteEmail(contactId,emailId)
        logger.info("Email deleted successfully")
        return res
    }

    @PutMapping("/{contactId}/email/{emailId}","/{contactId}/email/{emailId}/")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('manager') or hasRole('guest')  or hasRole('operator')")
    fun updateContactEmail(@PathVariable("contactId") contactId : Long, @PathVariable("emailId") emailId : Long,
        @RequestBody dto : EmailDTO) : SpecificContactDTO?{
        val res = contactService.updateEmail(contactId,emailId,dto)
        logger.info("Email updated successfully")
        return res
    }


    //Address : POST, UPDATE, DELETE
    @PostMapping("{contactId}/address", "{contactId}/address/")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('manager') or hasRole('guest')  or hasRole('operator')")
    fun addContactAddress(@PathVariable("contactId") id: Long, @RequestBody dto : AddressDTO) {
        contactService.addAddress(id, dto)
        logger.info("Address added to the contact successfully")
    }

    @DeleteMapping("/{contactId}/address/{addressId}","/{contactId}/email/{addressId}/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('manager') or hasRole('guest')  or hasRole('operator')")
    fun deleteContactAddress(@PathVariable("contactId") contactId : Long, @PathVariable("addressId") addressId : Long){
        val res = contactService.deleteAddress(contactId,addressId)
        logger.info("Address deleted successfully")
        return res
    }

    @PutMapping("/{contactId}/address/{addressId}","/{contactId}/address/{addressId}/")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('manager') or hasRole('guest')  or hasRole('operator')")
    fun updateContactAddress(@PathVariable("contactId") contactId : Long, @PathVariable("addressId") addressId : Long,
                           @RequestBody dto : AddressDTO) : SpecificContactDTO?{
        val res = contactService.updateAddress(contactId,addressId,dto)
        logger.info("Address updated successfully")
        return res
    }


    //Telephone : POST, UPDATE, DELETE

    @PostMapping("{contactId}/telephone", "{contactId}/telephone/")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('manager') or hasRole('guest')  or hasRole('operator')")
    fun addContactTelephone(@PathVariable("contactId") id: Long, @RequestBody dto : TelephoneDTO) {
        contactService.addTelephone(id, dto)
        logger.info("Telephone added to the contact successfully")
    }


    @DeleteMapping("/{contactId}/telephone/{telephoneId}","/{contactId}/telephone/{telephoneId}/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('manager') or hasRole('guest')  or hasRole('operator')")
    fun deleteContactTelephone(@PathVariable("contactId") contactId : Long, @PathVariable("telephoneId") telephoneId : Long){
        val res = contactService.deleteTelephone(contactId,telephoneId)
        logger.info("Telephone deleted successfully")
        return res
    }

    @PutMapping("/{contactId}/telephone/{telephoneId}","/{contactId}/telephone/{telephoneId}/")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('manager') or hasRole('guest')  or hasRole('operator')")
    fun updateContactTelephone(@PathVariable("contactId") contactId : Long, @PathVariable("telephoneId") telephoneId : Long,
                               @RequestBody dto : TelephoneDTO) : SpecificContactDTO?{
        val res = contactService.updateTelephone(contactId,telephoneId,dto)
        logger.info("Telephone updated successfully")
        return res
    }

    @PutMapping("{contactId}/","{contactId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('manager') or hasRole('guest')  or hasRole('operator')")
    fun updateContact(@PathVariable("contactId") id: Long, @RequestBody dto: GeneralContactDTO): SpecificContactDTO? {
        val res =  contactService.updateContact(id, null, dto)
        logger.info("Contact updated successfully")
        return res
    }

    @DeleteMapping("{contactId}/","{contactId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('manager') or hasRole('guest')  or hasRole('operator')")
    fun deleteContact(@PathVariable("contactId") id: Long){
        contactService.deleteContact(id, null)
        logger.info("Contact deleted successfully")
    }

}