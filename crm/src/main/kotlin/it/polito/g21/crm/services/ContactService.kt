package it.polito.g21.crm.services



import it.polito.g21.crm.entities.Address
import it.polito.g21.crm.entities.Email
import it.polito.g21.crm.entities.Telephone
import it.polito.g21.crm.models.*
import it.polito.g21.crm.utils.CategoryType
import it.polito.g21.crm.utils.EmploymentState

interface ContactService {

    fun getAllContacts(pageNumber : Int?, limit: Int?, name: String?, surname: String?, category: String?, ssnCode: String?,
                       email: String?, city: String?, country: String?, street: String?, telephone: String?,
                       skills: String?, employmentState: String?, location: String?, dailyRate: Double?) : List<SpecificContactDTO>

    fun createContact(contact : GeneralContactDTO, categoryType: CategoryType?) : GeneralContactDTO
    fun getContactById(id : Long, categoryType: CategoryType?) : SpecificContactDTO?

    fun addEmail(id: Long, dto: EmailDTO)

    fun updateCategory(id : Long, category: String, professionalDTO: ProfessionalDTO?) : GeneralContactDTO?

    fun deleteEmail(contactId : Long, emailId : Long)

    fun updateEmail(contactId: Long, emailId: Long, dto: EmailDTO) : SpecificContactDTO?

    fun addAddress(id: Long, dto: AddressDTO)

    fun deleteAddress(contactId: Long,addressId : Long)

    fun updateAddress(contactId: Long, addressId: Long, dto : AddressDTO) : SpecificContactDTO?

    fun addTelephone(id: Long, dto: TelephoneDTO)

    fun deleteTelephone(contactId: Long, telephoneId: Long)

    fun updateTelephone(contactId: Long, telephoneId: Long, dto: TelephoneDTO) : SpecificContactDTO?

    /*
    fun getContactEmails(contactId: Long) : List<EmailDTO>

    fun getContactAddresses(contactId: Long) : List<AddressDTO>

    fun getContactTelephones(contactId: Long) : List<TelephoneDTO> */

    fun findOrCreateEmail(email: EmailDTO) : Email

    fun findMailById(id : Long) : Email?

    fun deleteMail(id : Long)

    fun findOrCreateTelephone(number: String): Telephone

    fun findTelephoneById(id: Long): Telephone?

    fun deleteTelephone(id: Long)

    fun findOrCreateAddress(address: String) : Address

    fun findAddressById(id : Long) : Address?

    fun deleteAddress(id : Long)

    fun updateContact(id: Long, categoryType: CategoryType?, dto: GeneralContactDTO) : SpecificContactDTO?

    fun deleteContact(id: Long, categoryType: CategoryType?)

    fun addNotes(id: Long, categoryType: CategoryType, dto: NoteDTO)

    fun getNotesById(id: Long, categoryType: CategoryType) : List<NoteDTO>
}