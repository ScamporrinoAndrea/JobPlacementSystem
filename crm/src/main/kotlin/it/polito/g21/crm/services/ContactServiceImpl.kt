package it.polito.g21.crm.services

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.g21.crm.entities.*
import it.polito.g21.crm.exceptionhandler.*
import it.polito.g21.crm.models.*
import it.polito.g21.crm.models.topicdtos.toTopicDTO
import it.polito.g21.crm.repositories.*
import it.polito.g21.crm.utils.CategoryType
import it.polito.g21.crm.utils.EmploymentState
import it.polito.g21.crm.utils.JobStatus
import jakarta.transaction.Transactional
import org.springframework.data.domain.PageRequest
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.security.InvalidParameterException
import java.time.LocalDate
import java.time.LocalDateTime


@Service
@Transactional
class ContactServiceImpl(private val contactRepo : ContactRepo, val emailRepo : EmailRepo,
                         private val addressRepo: AddressRepo, private val telephoneRepo: TelephoneRepo,
                         private val customerRepo : CustomerRepo, private val professionalRepo: ProfessionalRepo,
                         private val noteRepo : NoteRepo, private val jobOfferRepo: JobOfferRepo,
                         private val actionOnJobRepo: ActionOnJobRepo, private val objectMapper: ObjectMapper,
                         private val kafkaTemplate: KafkaTemplate<String, String>
) : ContactService{
    // -----------------------------------------------------
    //Contact part
    // -----------------------------------------------------

    override fun getAllContacts(
        pageNumber: Int?,
        limit: Int?,
        name: String?,
        surname: String?,
        category: String?,
        ssncode: String?,
        email: String?,
        city: String?,
        country: String?,
        street: String?,
        telephone: String?,
        skills: String?,
        employmentState: String?,
        location: String?,
        dailyRate: Double?
    ): List<SpecificContactDTO> {
        val pageable = if(pageNumber != null && limit != null) {
            PageRequest.of(pageNumber-1, limit)
        } else {
            null
        }

        val finalCat = if(category!= null) {
            when(category) {
                "customer" -> CategoryType.CUSTOMER
                "professional" -> CategoryType.PROFESSIONAL
                else -> CategoryType.UNKNOWN
            }
        } else {
            null
        }

        val finalEmpState = if(employmentState!= null) {
            when(employmentState) {
                "employed" -> EmploymentState.EMPLOYED
                "available", "not employed" -> EmploymentState.AVAILABLE
                "not available" -> EmploymentState.NOT_AVAILABLE
                else -> throw InvalidParameterException("Invalid employmentState: $employmentState")
            }
        } else {
            null
        }


        return contactRepo.findAllFiltered(name, surname, finalCat, ssncode, email, city, country, street, telephone, skills, finalEmpState, location, dailyRate, pageable).map{ it.toFullDTO() }
    }

    override fun createContact(contact: GeneralContactDTO, categoryType: CategoryType?): GeneralContactDTO {
        when (contact.category) {
            "customer" -> {
                if(categoryType == CategoryType.CUSTOMER || categoryType == null) {
                    val cEntity = Contact(contact.name, contact.surname, CategoryType.CUSTOMER, contact.ssncode)
                    val customer = Customer(cEntity)
                    cEntity.customer = customer
                    contactRepo.save(cEntity)
                    customerRepo.save(customer)
                    val toSend = objectMapper.writeValueAsString(cEntity.toTopicDTO())
                    kafkaTemplate.send("newContact", toSend)
                    return cEntity.toDTO()
                }
                else{
                    throw InvalidCategoryException("The contact must be a ${categoryType.value}.")
                }
            }
            "professional" -> {
                if(categoryType == CategoryType.PROFESSIONAL || categoryType == null) {
                    if (contact.professionalInfo != null) {
                        val empState = when (contact.professionalInfo.employmentState) {
                            "employed" -> EmploymentState.EMPLOYED
                            "available", "unemployed" -> EmploymentState.AVAILABLE
                            "not available" -> EmploymentState.NOT_AVAILABLE
                            else -> throw InvalidEmpStateException("The employment state provided is not valid.")
                        }
                        val cEntity = Contact(contact.name, contact.surname, CategoryType.PROFESSIONAL, contact.ssncode)
                        val professional = Professional(
                            contact.professionalInfo.skills,
                            empState,
                            contact.professionalInfo.location,
                            contact.professionalInfo.dailyRate,
                            cEntity
                        )
                        cEntity.professional = professional
                        contactRepo.save(cEntity)
                        professionalRepo.save(professional)
                        val toSend = objectMapper.writeValueAsString(cEntity.toTopicDTO())
                        kafkaTemplate.send("newContact", toSend)
                        return cEntity.toDTO()
                    }
                    else{
                        throw InvalidCategoryException("Professional's information not provided.")
                    }
                }
                else{
                    throw InvalidCategoryException("The contact must be a ${categoryType.value}.")
                }
            }
            else -> {
                if(categoryType == null) {
                    val cEntity = Contact(contact.name, contact.surname, CategoryType.UNKNOWN, contact.ssncode)
                    contactRepo.save(cEntity)
                    return cEntity.toDTO()
                }
                else{
                    throw InvalidCategoryException("The contact must be a ${categoryType.value}.")
                }
            }
        }
    }

    override fun getContactById(id: Long, categoryType: CategoryType?): SpecificContactDTO? {
        val contact = contactRepo.findById(id).orElse(null) ?: throw ContactNotFoundException("Contact with id: $id not found")
        if(contact.category == categoryType || categoryType == null)
            return contact.toFullDTO()
        else{
            throw ContactNotFoundException("${categoryType.value} with id: $id not found")
        }
    }

    override fun addEmail(id: Long, dto: EmailDTO) {
        if(!Regex("[a-zA-Z0-9.]+@[a-zA-Z0-9.]+\\.[a-zA-Z]{2,}").matches(dto.mail))
            throw InvalidEmailException("The email provided is not in a valid format")
        val c = contactRepo.findById(id).orElse(null) ?: throw ContactNotFoundException("Contact with id: $id not found")
        val email = findOrCreateEmail(dto)
        if(c.emails.map{mail -> mail.id}.contains(email.id))
            throw EmailAlreadyPresentException("Mail already linked to this contact")
        c.addEmail(email)
        contactRepo.save(c)
    }

    override fun updateCategory(id: Long, category: String, dto: ProfessionalDTO?) : GeneralContactDTO? {
        val c = contactRepo.findById(id).orElse(null) ?: throw ContactNotFoundException("Contact with id: $id not found")
        when (category) {
            "customer" -> {
                if(c.category != CategoryType.CUSTOMER) {
                    //passo da professional a customer

                    //se il professional ha dei job non puo diventare customer
                    val professional = c.professional
                    if(professional?.jobs?.isNotEmpty() == true)
                        throw InvalidCategoryException("The professional cannot change the category since he has some jobOffer related to him")

                    c.category = CategoryType.CUSTOMER
                    val customer = Customer(c)
                    customerRepo.save(customer)
                    c.customer = customer
                    if(c.professional != null){
                        c.professional!!.getId()?.let { professionalRepo.deleteById(it) }
                        c.professional = null
                    }
                }
            }
            "professional" -> {
                if(c.category != CategoryType.PROFESSIONAL) {
                    //passo da customer a professional

                    //se il professional ha dei job non puo diventare customer
                    val customer = c.customer
                    if(customer?.jobs?.isNotEmpty() == true)
                        throw InvalidCategoryException("The customer cannot change the category since he has some jobOffer related to him")

                    if(dto != null) {
                        val empState = when (dto.employmentState) {
                            "employed" -> EmploymentState.EMPLOYED
                            "available", "unemployed" -> EmploymentState.AVAILABLE
                            "not available" -> EmploymentState.NOT_AVAILABLE
                            else -> throw InvalidEmpStateException("The employment state provided is not valid.")
                        }
                        c.category = CategoryType.PROFESSIONAL
                        val professional = Professional(dto.skills, empState, dto.location, dto.dailyRate, c)
                        professionalRepo.save(professional)
                        c.professional = professional
                        if(c.customer != null){
                            c.customer!!.getId()?.let { customerRepo.deleteById(it) }
                            c.customer = null
                        }
                    }
                    else{
                        throw InvalidCategoryException("Professional's information not provided.")
                    }
                }
                CategoryType.PROFESSIONAL
            }
            else -> {
                if(c.category != CategoryType.UNKNOWN) {

                    val customer = c.customer
                    val professional = c.professional
                    if(customer?.jobs?.isNotEmpty() == true)
                        throw InvalidCategoryException("The customer cannot change the category since he has some jobOffer related to him")
                    else if(professional?.jobs?.isNotEmpty() == true)
                        throw InvalidCategoryException("The professional cannot change the category since he has some jobOffer related to him")

                    c.category = CategoryType.UNKNOWN
                    if(c.professional?.getId() != null){
                        c.professional!!.getId()?.let { professionalRepo.deleteById(it) }
                        c.professional = null
                    }
                    else if(c.customer != null){
                        c.customer!!.getId()?.let { customerRepo.deleteById(it) }
                        c.customer = null
                    }
                }
            }
        }
        contactRepo.save(c)
        return c.toDTO()
    }

    override fun deleteEmail(contactId: Long, emailId: Long) {
        val c = contactRepo.findById(contactId).orElse(null) ?: throw ContactNotFoundException("Contact with id: $contactId not found")
        val mail = findMailById(emailId)
        if(mail != null) {
            c.deleteEmail(mail)
            if(mail.contacts.isEmpty())deleteMail(emailId)
        }
        else{
            throw EmailNotFoundException("Email with id: $emailId is not linked to contact with id: $contactId")
        }

    }

    override fun updateEmail(contactId: Long, emailId: Long, dto: EmailDTO): SpecificContactDTO? {
        if(!Regex("[a-zA-Z0-9.]+@[a-zA-Z0-9.]+\\.[a-zA-Z]{2,}").matches(dto.mail))
            throw InvalidEmailException("The email provided is not in a valid format")
        val c = contactRepo.findById(contactId).orElse(null) ?: throw ContactNotFoundException("Contact with id: $contactId not found")
        val mail = findMailById(emailId)
        val newMail = findOrCreateEmail(dto)
        if(c.emails.map{m -> m.id}.contains(newMail.id))
            throw EmailAlreadyPresentException("Mail already linked to this contact")
        if(mail != null) {
            c.deleteEmail(mail)
            c.addEmail(newMail)
            if(mail.contacts.isEmpty())deleteMail(emailId)
            contactRepo.save(c)
            return c.toFullDTO()
        }
        else{
            throw EmailNotFoundException("Email with id: $emailId is not linked to contact with id: $contactId")
        }
    }

    override fun addAddress(id: Long, dto: AddressDTO) {
        if(!Regex("[a-zA-Z\\s]+,\\s*[a-zA-Z0-9]+,\\s[a-zA-Z\\s]+,\\s[a-zA-Z\\s]+").matches(dto.address))
            throw InvalidAddressException("The address provided is not in a valid format")
        val c = contactRepo.findById(id).orElse(null) ?: throw ContactNotFoundException("Contact with id: $id not found")
        val address = findOrCreateAddress(dto.address)
        if(c.addresses.map{a -> a.id}.contains(address.id))
            throw AddressAlreadyPresentException("Address already linked to this contact")
        c.addAddress(address)
        contactRepo.save(c)
    }

    override fun deleteAddress(contactId: Long, addressId: Long) {
        val c = contactRepo.findById(contactId).orElse(null) ?: throw ContactNotFoundException("Contact with id: $contactId not found")
        val address = findAddressById(addressId)
        if(address != null) {
            c.deleteAddress(address)
            if(address.contacts.isEmpty())deleteAddress(addressId)
        }
        else{
            throw AddressNotFoundException("Address with id: $addressId is not linked to contact with id: $contactId")
        }
    }

    override fun updateAddress(contactId: Long, addressId: Long, dto: AddressDTO): SpecificContactDTO? {
        if(!Regex("[a-zA-Z\\s]+,\\s*[a-zA-Z0-9]+,\\s[a-zA-Z\\s]+,\\s[a-zA-Z\\s]+").matches(dto.address))
            throw InvalidAddressException("The address provided is not in a valid format")
        val c = contactRepo.findById(contactId).orElse(null) ?: throw ContactNotFoundException("Contact with id: $contactId not found")
        val address = findAddressById(addressId)
        val newAddress = findOrCreateAddress(dto.address)
        if(c.addresses.map{a -> a.id}.contains(newAddress.id))
            throw AddressAlreadyPresentException("Address already linked to this contact")
        if(address != null) {
            c.deleteAddress(address)
            c.addAddress(newAddress)
            if(address.contacts.isEmpty())deleteAddress(addressId)
            contactRepo.save(c)
            return c.toFullDTO()
        }
        else{
            throw AddressNotFoundException("Address with id: $addressId is not linked to contact with id: $contactId")
        }
    }

    override fun addTelephone(id: Long, dto: TelephoneDTO) {
        if(!Regex("^\\+?\\d+$").matches(dto.number))
            throw InvalidTelephoneException("The telephone number provided is not in a valid format")
        val c = contactRepo.findById(id).orElse(null) ?: throw ContactNotFoundException("Contact with id: $id not found")
        val telephone = findOrCreateTelephone(dto.number)
        if(c.telephones.map{t -> t.id}.contains(telephone.id))
            throw TelephoneAlreadyPresentException("Telephone already linked to this contact")
        c.addTelephone(telephone)
        contactRepo.save(c)
    }

    override fun deleteTelephone(contactId: Long, telephoneId: Long) {
        val c = contactRepo.findById(contactId).orElse(null) ?: throw ContactNotFoundException("Contact with id: $contactId not found")
        val telephone = findTelephoneById(telephoneId)
        if(telephone != null) {
            c.deleteTelephone(telephone)
            if(telephone.contacts.isEmpty())deleteTelephone(telephoneId)
        }
        else{
            throw TelephoneNotFoundException("Telephone with id: $telephoneId is not linked to contact with id: $contactId")
        }
    }

    override fun updateTelephone(contactId: Long, telephoneId: Long, dto: TelephoneDTO): SpecificContactDTO? {
        if(!Regex("^\\+?\\d+$").matches(dto.number))
            throw InvalidTelephoneException("The telephone number provided is not in a valid format")
        val c = contactRepo.findById(contactId).orElse(null) ?: throw ContactNotFoundException("Contact with id: $contactId not found")
        val telephone = findTelephoneById(telephoneId)
        val newTelephone = findOrCreateTelephone(dto.number)
        if(c.telephones.map{t -> t.id}.contains(newTelephone.id))
            throw TelephoneAlreadyPresentException("Telephone already linked to this contact")
        if(telephone != null) {
            c.deleteTelephone(telephone)
            c.addTelephone(newTelephone)
            if(telephone.contacts.isEmpty())deleteTelephone(telephoneId)
            contactRepo.save(c)
            return c.toFullDTO()
        }
        else{
            throw TelephoneNotFoundException("Telephone with id: $telephoneId is not linked to contact with id: $contactId")
        }
    }

    //todo: controllare jobOffer nell'updateContact
    override fun updateContact(id: Long, categoryType: CategoryType?, dto: GeneralContactDTO): SpecificContactDTO? {
        val contact = contactRepo.findById(id).orElse(null) ?: throw ContactNotFoundException("Contact with id: $id not found")
        val newCat = when (dto.category){
            "customer" -> CategoryType.CUSTOMER
            "professional" -> CategoryType.PROFESSIONAL
            else -> CategoryType.UNKNOWN
        }
        if((categoryType == null || categoryType==contact.category) && contact.category == newCat){
            contact.apply {
                name = dto.name
                surname = dto.surname
                SSNCode = dto.ssncode
            }
            if(contact.category == CategoryType.PROFESSIONAL && dto.professionalInfo != null){
                val empState = when (dto.professionalInfo.employmentState) {
                    "employed" -> EmploymentState.EMPLOYED
                    "available", "unemployed" -> {
                        val job = contact.professional?.jobs?.filter { it.status == JobStatus.CONSOLIDATED }
                        if(job != null){
                            if(job.isNotEmpty()){
                                job.map {
                                    it.status = JobStatus.ABORTED
                                    jobOfferRepo.save(it)
                                    val action = ActionOnJob(it.status, LocalDateTime.now(), "Professional changed his employment status", it, it.professional)
                                    actionOnJobRepo.save(action)
                                }
                            }
                        }
                        EmploymentState.AVAILABLE
                    }
                    "not available" -> {
                        val job = contact.professional?.jobs?.filter { it.status == JobStatus.CONSOLIDATED }
                        if(job != null){
                            if(job.isNotEmpty()){
                                job.map {
                                    it.status = JobStatus.ABORTED
                                    jobOfferRepo.save(it)
                                    val action = ActionOnJob(it.status, LocalDateTime.now(), "Professional changed his employment status", it, it.professional)
                                    actionOnJobRepo.save(action)
                                }
                            }
                        }
                        EmploymentState.NOT_AVAILABLE
                    }
                    else -> throw InvalidEmpStateException("The employment state provided is not valid.")
                }
                contact.professional?.apply {
                    skills = dto.professionalInfo.skills
                    employmentState = empState
                    location = dto.professionalInfo.location
                    dailyRate = dto.professionalInfo.dailyRate

                }
            }
            else if(contact.category == CategoryType.PROFESSIONAL && categoryType == CategoryType.PROFESSIONAL){
                throw InvalidCategoryException("Professional's information not provided.")
            }
        }
        else{
            throw InvalidCategoryException("Contact should not change category.")
        }
        contactRepo.save(contact)
        return contact.toFullDTO()
    }

    override fun deleteContact(id: Long, categoryType: CategoryType?) {
        val contact = contactRepo.findById(id).orElse(null) ?: throw ContactNotFoundException("Contact with id: $id not found")
        if(contact.category == categoryType || categoryType == null){
            if((contact.category == CategoryType.CUSTOMER && contact.customer?.jobs?.isEmpty() == true) ||
                (contact.category == CategoryType.PROFESSIONAL && contact.professional?.jobs?.isEmpty()  == true) ||
                contact.category == CategoryType.UNKNOWN)
                contactRepo.deleteById(id)
            else
                throw InvalidCategoryException("The ${contact.category.value} cannot be deleted since he has some jobOffer related to him.")

        }
        else{
            throw ContactNotFoundException("${categoryType.value} with id: $id not found")
        }
    }

    override fun addNotes(id: Long, categoryType: CategoryType, dto: NoteDTO) {
        val contact = contactRepo.findById(id).orElse(null) ?: throw ContactNotFoundException("Contact with id: $id not found")
        if(contact.category != categoryType) throw InvalidCategoryException("The contact must be a ${categoryType.value}.")
        val note = Note(LocalDate.now(), dto.note)
        when(categoryType){
            CategoryType.CUSTOMER -> contact.customer?.addNote(note)
            else -> contact.professional?.addNote(note)
        }
        noteRepo.save(note)
        contactRepo.save(contact)
    }

    override fun getNotesById(id: Long, categoryType: CategoryType): List<NoteDTO> {
        val contact = contactRepo.findById(id).orElse(null) ?: throw ContactNotFoundException("Contact with id: $id not found")
        if(contact.category != categoryType) throw InvalidCategoryException("The contact must be a ${categoryType.value}.")
        return noteRepo.getNotesByContactId(id, categoryType).map { it.toDTO() }
    }

    //---------------------------------
    //Email Part
    //---------------------------------
    override fun findOrCreateEmail(email: EmailDTO): Email {
        var res = emailRepo.findByMail(email.mail)
        if (res == null) {
            res = Email(mail = email.mail)
            emailRepo.save(res)
        }
        return res
    }

    override fun findMailById(id: Long): Email? {
        return emailRepo.findById(id).orElse(null)
    }

    override fun deleteMail(id: Long) {
        emailRepo.deleteById(id)
    }

    //---------------------------------
    //Telephone part
    //---------------------------------

    override fun findOrCreateTelephone(number: String): Telephone {
        var res = telephoneRepo.findByTelephoneDetails(number)
        if (res == null) {
            res = Telephone(null, number)
            telephoneRepo.save(res)
        }
        return res
    }

    override fun findTelephoneById(id: Long): Telephone? {
        return telephoneRepo.findById(id).orElse(null)
    }

    override fun deleteTelephone(id: Long) {
        telephoneRepo.deleteById(id)
    }

    //------------------------------------------
    //Address Part
    //-------------------------------------------

    override fun findOrCreateAddress(address: String): Address {
        val parts = address.split(",")
        var res = addressRepo.findByAddressDetails(parts[0].trim(),parts[1].trim(),parts[2].trim(), parts[3].trim())
        if(res == null){
            res = Address(null,parts[1].trim(),parts[0].trim(),parts[2].trim(),
                parts[3].trim())
            addressRepo.save(res)
        }
        return res
    }

    override fun findAddressById(id: Long): Address? {
        return addressRepo.findById(id).orElse(null)
    }

    override fun deleteAddress(id: Long) {
        addressRepo.deleteById(id)
    }



}