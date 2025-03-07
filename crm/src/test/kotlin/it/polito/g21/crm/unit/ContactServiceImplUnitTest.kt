package it.polito.g21.crm.unit

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import it.polito.g21.crm.entities.*
import it.polito.g21.crm.exceptionhandler.*
import it.polito.g21.crm.models.*
import it.polito.g21.crm.repositories.*
import it.polito.g21.crm.services.ContactServiceImpl
import it.polito.g21.crm.utils.CategoryType
import it.polito.g21.crm.utils.EmploymentState
import it.polito.g21.crm.utils.JobStatus
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageRequest
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

internal class ContactServiceImplUnitTest {
    private val repo1 = mockk<ContactRepo>()
    private val repo2 = mockk<EmailRepo>()
    private val repo3 = mockk<AddressRepo>()
    private val repo4 = mockk<TelephoneRepo>()
    private val repo5 = mockk<CustomerRepo>()
    private val repo6 = mockk<ProfessionalRepo>()
    private val repo7 = mockk<NoteRepo>()
    private val repo8 = mockk<JobOfferRepo>()
    private val repo9 = mockk<ActionOnJobRepo>()

    private val myDate = LocalDate.now()
    private val myDateTime = LocalDateTime.now()

    private val c1 = Contact("Mario", "Rossi", CategoryType.CUSTOMER, "123456789")
    private val c2 = Contact("Mario", "Verdi", CategoryType.PROFESSIONAL, "123456788")
    private val c3 = Contact("Mario", "Rossi", CategoryType.UNKNOWN, "123456789")

    private val pi1 = ProfessionalDTO("testSkill", "employed", "testLocation", 4.5, null)
    private val pi2 = ProfessionalDTO("testSkill", "not available", "testLocation", 4.5, null)
    private val piErr = ProfessionalDTO("testSkill", "error", "testLocation", 4.5, null)
    private val cu1 = Customer(c1)
    private val p1 = Professional(pi1.skills, EmploymentState.EMPLOYED, pi1.location, pi1.dailyRate, c2)

    private val c1DTO = GeneralContactDTO(null, "Mario","Rossi", "customer", "123456789", null)
    private val c2DTO = GeneralContactDTO(null, "Mario","Verdi", "professional", "123456789", pi1)
    private val c3DTO = GeneralContactDTO(null, "Mario","Rossi", "unknown", "123456789", null)
    private val c4DTO = GeneralContactDTO(null, "Mario","Verdi", "professional", "123456789", pi2)
    private val cErr1 = GeneralContactDTO(null, "Mario","Rossi", "professional", "123456789", null)
    private val cErr2 = GeneralContactDTO(null, "Mario","Rossi", "professional", "123456789", piErr)

    private val m1 = Email(1, "test@test.com")
    private val m2 = Email(2, "another.test@test.com")

    private val m1DTO = EmailDTO(null, "test@test.com")
    private val m2DTO = EmailDTO(null, "another.test@test.com")

    private val a1 = Address(1, "1", "testName", "testCity",  "testCountry")
    private val a2 = Address(2, "2", "testName", "anotherTestCity", "testCountry")

    private val a1DTO = AddressDTO(null, "testName, 1, testCity, testCountry")
    private val a2DTO = AddressDTO(null, "testName, 2, anotherTestCity, testCountry")

    private val t1 = Telephone(1, "1234567890")
    private val t2 = Telephone(2, "1234567899")

    private val t1DTO = TelephoneDTO(null, "1234567890")
    private val t2DTO = TelephoneDTO(null, "1234567899")

    private val n1 = Note(myDate, "testNote1")
    private val n2 = Note(myDate, "testNote2")
    private val n1DTO = NoteDTO("testNote1", null)

    private val cu = Customer(c1)

    private val j1 = JobOffer("testDesc", "testSkills", 1, 1.2, cu)
    private val action1 = ActionOnJob(JobStatus.SELECTION_PHASE, myDateTime, "testNote", j1, null)

    // getAllContacts
    @Test
    fun `it should retrieve all contacts by a List of ContactDTO`(){
        // Arrange

        every{
            repo1.findAllFiltered(null, null, null, null, null, null, null, null, null, null, null, null, null, null)
        } answers {
            listOf(c1, c2)
        }
        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        // Act
        val res = service.getAllContacts(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null)


        // Assert
        assert(res.size ==2)
        assert(res[0].name == "Mario")
        assert(res[0].surname == "Rossi")
        assert(res[0].category == "customer")
        assert(res[1].name == "Mario")
        assert(res[1].surname == "Verdi")
        assert(res[1].category == "professional")
    }

    @Test
    fun `it should retrieve all contacts with filters by a List of ContactDTO`(){
        // Arrange

        every{
            repo1.findAllFiltered("Mario", "Rossi", CategoryType.CUSTOMER, null, null, null, null, null, null, null, null, null, null, null)
        } answers {
            listOf(c1)
        }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        // Act

        val res = service.getAllContacts(null, null, "Mario", "Rossi", "customer", null, null, null, null, null, null, null, null, null, null)


        // Assert
        assert(res.size == 1)
        assert(res[0].name == "Mario")
        assert(res[0].surname == "Rossi")
        assert(res[0].category =="customer")
    }

    @Test
    fun `it should retrieve all professionals with filters for professional info by a List of ContactDTO`(){
        // Arrange

        every{
            repo1.findAllFiltered("Mario", "Verdi", CategoryType.PROFESSIONAL, null, null, null, null, null, null, "testSkill", EmploymentState.EMPLOYED, "testLocation", 4.5, null)
        } answers {
            c2.professional = p1
            listOf(c2)
        }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        // Act

        val res = service.getAllContacts(null, null, "Mario", "Verdi", "professional", null, null, null, null, null, null, "testSkill", "employed", "testLocation", 4.5)


        // Assert
        assert(res.size == 1)
        assert(res[0].name == "Mario")
        assert(res[0].surname == "Verdi")
        assert(res[0].category =="professional")
        assert(res[0].professionalInfo?.skills == "testSkill")
        assert(res[0].professionalInfo?.employmentState == "employed")
        assert(res[0].professionalInfo?.location == "testLocation")
        assert(res[0].professionalInfo?.dailyRate == 4.5)

    }

    @Test
    fun `it should retrieve all contacts with filters and pagination by a List of ContactDTO`(){
        // Arrange

        every{
            repo1.findAllFiltered("Mario", null, null, null, null, null, null, null, null, null, null, null, null, PageRequest.of(0, 2))
        } answers {
            listOf(c1, c2)
        }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        // Act
        val res = service.getAllContacts(1, 2, "Mario", null, null, null, null, null, null, null, null, null, null, null, null)


        // Assert
        assert(res.size == 2)
        assert(res[0].name == "Mario")
        assert(res[0].surname == "Rossi")
        assert(res[0].category == "customer")
        assert(res[0].ssncode == "123456789")
        assert(res[1].name == "Mario")
        assert(res[1].surname == "Verdi")
        assert(res[1].category == "professional")
        assert(res[1].ssncode =="123456788")
    }
   // createContact
    @Test
    fun `it should create a contact entity`() {
        // Arrange
        every { repo1.save(any()) } answers { c3 }
        every { repo5.save(any()) } answers { cu1 }
        every { repo6.save(any()) } answers { p1 }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), repo5, repo6, mockk(), mockk(), mockk())

        // Act
        service.createContact(c3DTO, null)

        // Assert
        verify { repo1.save(any()) }
        verify(exactly = 0) { repo5.save(any()) }
        verify(exactly = 0) { repo6.save(any()) }
    }

    @Test
    fun `it should create a customer entity`() {
        // Arrange
        every { repo1.save(any()) } answers { c1 }
        every { repo5.save(any()) } answers { cu1 }
        every { repo6.save(any()) } answers { p1 }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), repo5, repo6, mockk(), mockk(), mockk())

        // Act
        service.createContact(c1DTO, CategoryType.CUSTOMER)

        // Assert
        verify { repo1.save(any()) }
        verify { repo5.save(any()) }
        verify(exactly = 0) { repo6.save(any()) }
    }

    @Test
    fun `it should create a professional Entity`() {
        // Arrange
        every { repo1.save(any()) } answers { c2 }
        every { repo5.save(any()) } answers { cu1 }
        every { repo6.save(any()) } answers { p1 }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), repo5, repo6, mockk(), mockk(), mockk())

        // Act
        service.createContact(c2DTO, CategoryType.PROFESSIONAL)

        // Assert
        verify { repo1.save(any()) }
        verify(exactly = 0) { repo5.save(any()) }
        verify { repo6.save(any()) }
    }

    @Test
    fun `it should raise an error finding 'customer' category rather than 'professional'`() {
        // Arrange
        every { repo1.save(any()) } answers { c1 }
        every { repo5.save(any()) } answers { cu1 }
        every { repo6.save(any()) } answers { p1 }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), repo5, repo6, mockk(), mockk(), mockk())

        // Act & Assert
        assertThrows<InvalidCategoryException> {
            service.createContact(c1DTO, CategoryType.PROFESSIONAL)
        }
        verify(exactly = 0) { repo1.save(any()) }
        verify(exactly = 0) { repo5.save(any()) }
        verify(exactly = 0) { repo6.save(any()) }
    }

    @Test
    fun `it should raise an error finding 'professional' category rather than 'customer'`() {
        // Arrange
        every { repo1.save(any()) } answers { c1 }
        every { repo5.save(any()) } answers { cu1 }
        every { repo6.save(any()) } answers { p1 }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), repo5, repo6, mockk(), mockk(), mockk())

        // Act & Assert
        assertThrows<InvalidCategoryException> {
            service.createContact(c2DTO, CategoryType.CUSTOMER)
        }
        verify(exactly = 0) { repo1.save(any()) }
        verify(exactly = 0) { repo5.save(any()) }
        verify(exactly = 0) { repo6.save(any()) }
    }

    @Test
    fun `it should raise an error finding 'unknown' category rather than 'professional'`() {
        // Arrange
        every { repo1.save(any()) } answers { c3 }
        every { repo5.save(any()) } answers { cu1 }
        every { repo6.save(any()) } answers { p1 }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), repo5, repo6, mockk(), mockk(), mockk())


        // Assert
        assertThrows<InvalidCategoryException> {
            service.createContact(c1DTO, CategoryType.PROFESSIONAL)
        }
        verify(exactly = 0) { repo1.save(any()) }
        verify(exactly = 0) { repo5.save(any()) }
        verify(exactly = 0) { repo6.save(any()) }
    }

    @Test
    fun `it should raise an error not having professional info in creation`() {
        // Arrange
        every { repo1.save(any()) } answers { c3 }
        every { repo5.save(any()) } answers { cu1 }
        every { repo6.save(any()) } answers { p1 }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), repo5, repo6, mockk(), mockk(), mockk())

        // Assert
        assertThrows<InvalidCategoryException> {
            service.createContact(cErr1, CategoryType.PROFESSIONAL)
        }
        verify(exactly = 0) { repo1.save(any()) }
        verify(exactly = 0) { repo5.save(any()) }
        verify(exactly = 0) { repo6.save(any()) }
    }

    @Test
    fun `it should raise an error having invalid emp state in creation`() {
        // Arrange
        every { repo1.save(any()) } answers { c3 }
        every { repo5.save(any()) } answers { cu1 }
        every { repo6.save(any()) } answers { p1 }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), repo5, repo6, mockk(), mockk(), mockk())


        // Assert
        assertThrows<InvalidEmpStateException> {
            service.createContact(cErr2, CategoryType.PROFESSIONAL)
        }
        verify(exactly = 0) { repo1.save(any()) }
        verify(exactly = 0) { repo5.save(any()) }
        verify(exactly = 0) { repo6.save(any()) }
    }



    // getContactById
    @Test
    fun `it should correctly retrieve the Contact`() {
        // Arrange
        every { repo1.findById(any()) } answers { Optional.of(c3) }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        // Act
        val res = service.getContactById(1, null)

        // Assert
        assertNotNull(res)
        res?.let {
            assert(it.name == "Mario")
            assert(it.surname == "Rossi")
            assert(it.category == "unknown")
        }
    }

    @Test
    fun `it should correctly retrieve the Customer`() {
        // Arrange
        every { repo1.findById(any()) } answers { Optional.of(c1) }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        // Act
        val res = service.getContactById(1, CategoryType.CUSTOMER)

        // Assert
        assertNotNull(res)
        res?.let {
            assert(it.name == "Mario")
            assert(it.surname == "Rossi")
            assert(it.category == "customer")
        }
    }

    @Test
    fun `it should correctly retrieve the Professional`() {
        // Arrange
        every { repo1.findById(any()) } answers { Optional.of(c2) }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        // Act
        val res = service.getContactById(1, CategoryType.PROFESSIONAL)

        // Assert
        assertNotNull(res)
        res?.let {
            assert(it.name == "Mario")
            assert(it.surname == "Verdi")
            assert(it.category == "professional")
        }
    }

    @Test
    fun `it should raise an error not finding the Contact`() {
        // Arrange
        every { repo1.findById(any()) } returns Optional.empty()

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        // Act & Assert
        assertThrows<ContactNotFoundException> {
            service.getContactById(1, null)
        }
    }

    @Test
    fun `it should raise an error finding a Contact that it is not a Customer`() {
        // Arrange
        every { repo1.findById(any()) } answers { Optional.of(c2) }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        // Act & Assert
        assertThrows<ContactNotFoundException> {
            service.getContactById(1, CategoryType.CUSTOMER)
        }
    }

    @Test
    fun `it should raise an error finding a Contact that it is not a Professional`() {
        // Arrange
        every { repo1.findById(any()) } answers { Optional.of(c1) }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        // Act & Assert
        assertThrows<ContactNotFoundException> {
            service.getContactById(1, CategoryType.PROFESSIONAL)
        }
    }

    // updateCategory
    @Test
    fun `it should correctly update the category into customer`() {
        // Arrange
        every { repo1.findById(any()) } answers { Optional.of(c2) }
        every { repo1.save(any()) } answers { c1 }
        every { repo5.save(any()) } answers { cu1 }
        every { repo6.save(any()) } answers { p1 }
        every { repo5.deleteById(any()) } answers {}
        every { repo6.deleteById(any()) } answers {}

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), repo5, repo6, mockk(), mockk(), mockk())

        // Act

        val res = service.updateCategory(1, "customer", null)

        // Assert
        assertNotNull(res)
        res?.let {
            assert(it.name == "Mario")
            assert(it.surname == "Verdi")
            assert(it.category == "customer")
        }
        verify { repo5.save(any()) }
        verify(exactly = 0) { repo6.save(any()) }
    }

    @Test
    fun `it should correctly update the category into professional`() {
        // Arrange
        every { repo1.findById(any()) } answers { Optional.of(c1) }
        every { repo1.save(any()) } answers { c1 }
        every { repo5.save(any()) } answers { cu1 }
        every { repo6.save(any()) } answers { p1 }
        every { repo5.deleteById(any()) } answers {}
        every { repo6.deleteById(any()) } answers {}

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), repo5, repo6, mockk(), mockk(), mockk())

        // Act

        val res = service.updateCategory(1, "professional", pi1)

        // Assert
        assertNotNull(res)
        res?.let {
            assert(it.name == "Mario")
            assert(it.surname == "Rossi")
            assert(it.category == "professional")
            assert(it.professionalInfo != null)
        }
        verify { repo6.save(any()) }
        verify(exactly = 0) { repo5.save(any()) }
    }

    @Test
    fun `it should correctly update the category into unknown`() {
        // Arrange
        every { repo1.findById(any()) } answers { Optional.of(c1) }
        every { repo1.save(any()) } answers { c1 }
        every { repo5.save(any()) } answers { cu1 }
        every { repo6.save(any()) } answers { p1 }
        every { repo5.deleteById(any()) } answers {}
        every { repo6.deleteById(any()) } answers {}

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), repo5, repo6, mockk(), mockk(), mockk())

        // Act

        val res = service.updateCategory(1, "unknown", null)

        // Assert
        assertNotNull(res)
        res?.let {
            assert(it.name == "Mario")
            assert(it.surname == "Rossi")
            assert(it.category == "unknown")
        }
        verify(exactly = 0) { repo5.save(any()) }
        verify(exactly = 0) { repo6.save(any()) }
    }

    @Test
    fun `it should raise an error not finding the Contact trying to change the category`() {
        // Arrange
        every { repo1.findById(any()) } returns Optional.empty()
        every { repo5.save(any()) } answers { cu1 }
        every { repo6.save(any()) } answers { p1 }
        every { repo5.deleteById(any()) } answers {}
        every { repo6.deleteById(any()) } answers {}

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), repo5, repo6, mockk(), mockk(), mockk())

        // Act & Assert
        assertThrows<ContactNotFoundException> {
            service.updateCategory(1, "customer", null)
        }
        verify(exactly = 0) { repo5.save(any()) }
        verify(exactly = 0) { repo6.save(any()) }
    }

    @Test
    fun `it should raise an error not having profession info trying to change category into professional`() {
        // Arrange
        every { repo1.findById(any()) } answers { Optional.of(c1) }
        every { repo5.save(any()) } answers { cu1 }
        every { repo6.save(any()) } answers { p1 }
        every { repo5.deleteById(any()) } answers {}
        every { repo6.deleteById(any()) } answers {}

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), repo5, repo6, mockk(), mockk(), mockk())

        // Act & Assert
        assertThrows<InvalidCategoryException> {
            service.updateCategory(1, "professional", null)
        }
        verify(exactly = 0) { repo5.save(any()) }
        verify(exactly = 0) { repo6.save(any()) }
    }

    @Test
    fun `it should raise an error finding an invalid emp state trying to change contact's category`() {
        // Arrange
        every { repo1.findById(any()) } answers { Optional.of(c1) }
        every { repo5.save(any()) } answers { cu1 }
        every { repo6.save(any()) } answers { p1 }
        every { repo5.deleteById(any()) } answers {}
        every { repo6.deleteById(any()) } answers {}

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), repo5, repo6, mockk(), mockk(), mockk())

        // Act & Assert
        assertThrows<InvalidEmpStateException> {
            service.updateCategory(1, "professional", piErr)
        }
        verify(exactly = 0) { repo5.save(any()) }
        verify(exactly = 0) { repo6.save(any()) }
    }

    // addEmail

    @Test
    fun `it should correctly add a new mail`() {
        // Arrange
        every { repo1.findById(any()) } answers { Optional.of(c1) }
        every { repo1.save(any()) } answers { c1 }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        every{ service.findOrCreateEmail(m1DTO) } answers { m1 }

        // Act

        service.addEmail(1, m1DTO)

        // Act & Assert
        verify { repo1.save(any()) }
    }

    @Test
    fun `it should raise an error not finding the Contact trying to add a new mail`() {
        // Arrange
        every { repo1.findById(any()) } returns Optional.empty()

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        // Act & Assert
        assertThrows<ContactNotFoundException> {
            service.addEmail(1, m1DTO)
        }
    }

    @Test
    fun `it should raise an error due to the fact that the mail is already linked trying to add a new mail`() {
        // Arrange
        every { repo1.findById(any()) } answers {
            c1.addEmail(m1)
            Optional.of(c1)
        }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        every{ service.findOrCreateEmail(m1DTO) } answers { m1 }

        // Act & Assert
        assertThrows<EmailAlreadyPresentException> {
            service.addEmail(1, m1DTO)
        }
    }

    // updateEmail
    @Test
    fun `it should correctly update a mail`() {
        // Arrange
        every { repo1.findById(any()) } answers {
            c1.addEmail(m1)
            Optional.of(c1) }
        every { repo1.save(any()) } answers { c1 }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        every{ service.findOrCreateEmail(m2DTO) } answers { m2 }
        every { service.findMailById(1) } answers { m1 }
        every { service.deleteMail(1) } answers { }

        // Act

        val res = service.updateEmail(1, 1, m2DTO)

        // Act & Assert
        verify { repo1.save(any()) }
        if (res != null) {
            assert(res.mailList[0].mail == "another.test@test.com")
        }
    }

    @Test
    fun `it should raise an error not finding the Contact trying to update a mail`() {
        // Arrange
        every { repo1.findById(any()) } returns Optional.empty()

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        // Act & Assert
        assertThrows<ContactNotFoundException> {
            service.updateEmail(1, 1, m2DTO)
        }
    }

    @Test
    fun `it should raise an error due to the fact that the mail is already linked trying to update a mail`() {
        // Arrange
        every { repo1.findById(any()) } answers {
            c1.addEmail(m2)
            Optional.of(c1)
        }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        every{ service.findOrCreateEmail(m2DTO) } answers { m2 }
        every { service.findMailById(1) } answers { m1 }
        every { service.deleteMail(1) } answers { }

        // Act & Assert
        assertThrows<EmailAlreadyPresentException> {
            service.updateEmail(1, 1, m2DTO)
        }
    }

    @Test
    fun `it should raise an error due to the fact that the mail is not found trying to update a mail`() {
        // Arrange
        every { repo1.findById(any()) } answers {
            Optional.of(c1)
        }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        every{ service.findOrCreateEmail(m2DTO) } answers { m2 }
        every { service.findMailById(1) } answers { null }

        // Act & Assert
        assertThrows<EmailNotFoundException> {
            service.updateEmail(1, 1, m2DTO)
        }
    }

    // deleteEmail

    @Test
    fun `it should correctly delete an email`() {
        // Arrange
        every { repo1.findById(any()) } answers {
            c1.addEmail(m1)
            Optional.of(c1) }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        every { service.findMailById(1) } answers { m1 }
        every { service.deleteMail(1) } answers { }

        // Act

        service.deleteEmail(1, 1)

        // Assert
        verify { service.deleteMail(1) }
    }

    @Test
    fun `it should raise an error not finding the Contact trying to delete a mail`() {
        // Arrange
        every { repo1.findById(any()) } returns Optional.empty()

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        // Act & Assert
        assertThrows<ContactNotFoundException> {
            service.deleteEmail(1, 1)
        }
    }

    @Test
    fun `it should raise an error due to the fact that the mail is not found trying to delete a mail`() {
        // Arrange
        every { repo1.findById(any()) } answers {
            Optional.of(c1)
        }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        every{ service.findOrCreateEmail(m2DTO) } answers { m2 }
        every { service.findMailById(1) } answers { null }

        // Act & Assert
        assertThrows<EmailNotFoundException> {
            service.deleteEmail(1, 1)
        }
    }

    // addAddress

    @Test
    fun `it should correctly add a new address`() {
        // Arrange
        every { repo1.findById(any()) } answers { Optional.of(c1) }
        every { repo1.save(any()) } answers { c1 }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        every{ service.findOrCreateAddress(a1DTO.address) } answers { a1 }

        // Act

        service.addAddress(1, a1DTO)

        // Act & Assert
        verify { repo1.save(any()) }
    }

    @Test
    fun `it should raise an error not finding the Contact trying to add a new address`() {
        // Arrange
        every { repo1.findById(any()) } returns Optional.empty()

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        // Act & Assert
        assertThrows<ContactNotFoundException> {
            service.addAddress(1, a1DTO)
        }
    }

    @Test
    fun `it should raise an error due to the fact that the address is already linked trying to add a new address`() {
        // Arrange
        every { repo1.findById(any()) } answers {
            c1.addAddress(a1)
            Optional.of(c1)
        }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        every{ service.findOrCreateAddress(a1DTO.address) } answers { a1 }

        // Act & Assert
        assertThrows<AddressAlreadyPresentException> {
            service.addAddress(1, a1DTO)
        }
    }

    // updateAddress
    @Test
    fun `it should correctly update an address`() {
        // Arrange
        every { repo1.findById(any()) } answers {
            c1.addAddress(a1)
            Optional.of(c1) }
        every { repo1.save(any()) } answers { c1 }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        every{ service.findOrCreateAddress(a2DTO.address) } answers { a2 }
        every { service.findAddressById(1) } answers { a1 }
        every { service.deleteAddress(1) } answers { }

        // Act

        val res = service.updateAddress(1, 1, a2DTO)

        // Act & Assert
        verify { repo1.save(any()) }
        if (res != null) {
            assert(res.addressList[0].address == a2DTO.address)
        }
    }

    @Test
    fun `it should raise an error not finding the Contact trying to update an address`() {
        // Arrange
        every { repo1.findById(any()) } returns Optional.empty()

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        // Act & Assert
        assertThrows<ContactNotFoundException> {
            service.updateAddress(1, 1, a2DTO)
        }
    }

    @Test
    fun `it should raise an error due to the fact that the address is already linked trying to update an address`() {
        // Arrange
        every { repo1.findById(any()) } answers {
            c1.addAddress(a2)
            Optional.of(c1)
        }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        every{ service.findOrCreateAddress(a2DTO.address) } answers { a2 }
        every { service.findAddressById(1) } answers { a1 }
        every { service.deleteAddress(1) } answers { }

        // Act & Assert
        assertThrows<AddressAlreadyPresentException> {
            service.updateAddress(1, 1, a2DTO)
        }
    }

    @Test
    fun `it should raise an error due to the fact that the address is not found trying to update an address`() {
        // Arrange
        every { repo1.findById(any()) } answers {
            Optional.of(c1)
        }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        every{ service.findOrCreateAddress(a2DTO.address) } answers { a2 }
        every { service.findAddressById(1) } answers { null }

        // Act & Assert
        assertThrows<AddressNotFoundException> {
            service.updateAddress(1, 1, a2DTO)
        }
    }

    // deleteAddress

    @Test
    fun `it should correctly delete an address`() {
        // Arrange
        every { repo1.findById(any()) } answers {
            c1.addAddress(a1)
            Optional.of(c1) }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        every { service.findAddressById(1) } answers { a1 }
        every { service.deleteAddress(1) } answers { }

        // Act

        service.deleteAddress(1, 1)

        // Assert
        verify { service.deleteAddress(1) }
    }

    @Test
    fun `it should raise an error not finding the Contact trying to delete a address`() {
        // Arrange
        every { repo1.findById(any()) } returns Optional.empty()

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        // Act & Assert
        assertThrows<ContactNotFoundException> {
            service.deleteAddress(1, 1)
        }
    }

    @Test
    fun `it should raise an error due to the fact that the address is not found trying to delete a address`() {
        // Arrange
        every { repo1.findById(any()) } answers {
            Optional.of(c1)
        }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        every{ service.findOrCreateAddress(a2DTO.address) } answers { a2 }
        every { service.findAddressById(1) } answers { null }

        // Act & Assert
        assertThrows<AddressNotFoundException> {
            service.deleteAddress(1, 1)
        }
    }

    // addTelephone

    @Test
    fun `it should correctly add a new telephone`() {
        // Arrange
        every { repo1.findById(any()) } answers { Optional.of(c1) }
        every { repo1.save(any()) } answers { c1 }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        every{ service.findOrCreateTelephone(t1DTO.number) } answers { t1 }

        // Act

        service.addTelephone(1, t1DTO)

        // Act & Assert
        verify { repo1.save(any()) }
    }

    @Test
    fun `it should raise an error not finding the Contact trying to add a new telephone`() {
        // Arrange
        every { repo1.findById(any()) } returns Optional.empty()

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        // Act & Assert
        assertThrows<ContactNotFoundException> {
            service.addTelephone(1, t1DTO)
        }
    }

    @Test
    fun `it should raise an error due to the fact that the telephone is already linked trying to add a new telephone`() {
        // Arrange
        every { repo1.findById(any()) } answers {
            c1.addTelephone(t1)
            Optional.of(c1)
        }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        every{ service.findOrCreateTelephone(t1DTO.number) } answers { t1 }

        // Act & Assert
        assertThrows<TelephoneAlreadyPresentException> {
            service.addTelephone(1, t1DTO)
        }
    }

    // updateTelephone
    @Test
    fun `it should correctly update a telephone`() {
        // Arrange
        every { repo1.findById(any()) } answers {
            c1.addTelephone(t1)
            Optional.of(c1) }
        every { repo1.save(any()) } answers { c1 }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        every{ service.findOrCreateTelephone(t2DTO.number) } answers { t2 }
        every { service.findTelephoneById(1) } answers { t1 }
        every { service.deleteTelephone(1) } answers { }

        // Act

        val res = service.updateTelephone(1, 1, t2DTO)

        // Act & Assert
        verify { repo1.save(any()) }
        if (res != null) {
            assert(res.telephoneList[0].number == t2DTO.number)
        }
    }

    @Test
    fun `it should raise an error not finding the Contact trying to update a telephone`() {
        // Arrange
        every { repo1.findById(any()) } returns Optional.empty()

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        // Act & Assert
        assertThrows<ContactNotFoundException> {
            service.updateTelephone(1, 1, t2DTO)
        }
    }

    @Test
    fun `it should raise an error due to the fact that the telephone is already linked trying to update a telephone`() {
        // Arrange
        every { repo1.findById(any()) } answers {
            c1.addTelephone(t2)
            Optional.of(c1)
        }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        every{ service.findOrCreateTelephone(t2DTO.number) } answers { t2 }
        every { service.findTelephoneById(1) } answers { t1 }
        every { service.deleteTelephone(1) } answers { }

        // Act & Assert
        assertThrows<TelephoneAlreadyPresentException> {
            service.updateTelephone(1, 1, t2DTO)
        }
    }

    @Test
    fun `it should raise an error due to the fact that the telephone is not found trying to update a telephone`() {
        // Arrange
        every { repo1.findById(any()) } answers {
            Optional.of(c1)
        }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        every{ service.findOrCreateTelephone(t2DTO.number) } answers { t2 }
        every { service.findTelephoneById(1) } answers { null }

        // Act & Assert
        assertThrows<TelephoneNotFoundException> {
            service.updateTelephone(1, 1, t2DTO)
        }
    }

    // deleteTelephone

    @Test
    fun `it should correctly delete an telephone`() {
        // Arrange
        every { repo1.findById(any()) } answers {
            c1.addTelephone(t1)
            Optional.of(c1) }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        every { service.findTelephoneById(1) } answers { t1 }
        every { service.deleteTelephone(1) } answers { }

        // Act

        service.deleteTelephone(1, 1)

        // Assert
        verify { service.deleteTelephone(1) }
    }

    @Test
    fun `it should raise an error not finding the Contact trying to delete a telephone`() {
        // Arrange
        every { repo1.findById(any()) } returns Optional.empty()

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        // Act & Assert
        assertThrows<ContactNotFoundException> {
            service.deleteTelephone(1, 1)
        }
    }

    @Test
    fun `it should raise an error due to the fact that the telephone is not found trying to delete a telephone`() {
        // Arrange
        every { repo1.findById(any()) } answers {
            Optional.of(c1)
        }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        every{ service.findOrCreateTelephone(t2DTO.number) } answers { t2 }
        every { service.findTelephoneById(1) } answers { null }

        // Act & Assert
        assertThrows<TelephoneNotFoundException> {
            service.deleteTelephone(1, 1)
        }
    }

    // findOrCreateEmail
    @Test
    fun `it should return the Email found`() {
        // Arrange
        every { repo2.findByMail(m1DTO.mail) } answers {
            m1
        }
        every { repo2.save(any()) } answers { m1 }

        val service = ContactServiceImpl(mockk(), repo2, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        // Act

        val res = service.findOrCreateEmail(m1DTO)

        // Assert
        assert(res.id == m1.id)
        verify(exactly = 0) { repo2.save(any()) }
    }

    @Test
    fun `it should return the new Email created`() {
        // Arrange
        every { repo2.findByMail(m1DTO.mail) } answers {
            null
        }
        every { repo2.save(any()) } answers { m1 }

        val service = ContactServiceImpl(mockk(), repo2, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        // Act

        val res = service.findOrCreateEmail(m1DTO)

        // Assert
        assert(res.mail == m1DTO.mail)
        verify { repo2.save(any()) }
    }

    // findOrCreateAddress
    @Test
    fun `it should return the Address found`() {
        // Arrange
        every { repo3.findByAddressDetails("testName", "1", "testCity", "testCountry") } answers {
            a1
        }
        every { repo3.save(any()) } answers { a1 }

        val service = ContactServiceImpl(mockk(), mockk(), repo3, mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        // Act

        val res = service.findOrCreateAddress(a1DTO.address)

        // Assert
        assert(res.id == a1.id)
        verify(exactly = 0) { repo3.save(any()) }
    }

    @Test
    fun `it should return the new Address created`() {
        // Arrange
        every { repo3.findByAddressDetails("testName", "1", "testCity", "testCountry") } answers {
            null
        }
        every { repo3.save(any()) } answers { a1 }

        val service = ContactServiceImpl(mockk(), mockk(), repo3, mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        // Act

        val res = service.findOrCreateAddress(a1DTO.address)

        // Assert
        assert(res.city == a1.city)
        assert(res.streetNumber == a1.streetNumber)
        assert(res.streetName == a1.streetName)
        assert(res.country == a1.country)
        verify { repo3.save(any()) }
    }

    // findOrCreateTelephone
    @Test
    fun `it should return the Telephone found`() {
        // Arrange
        every { repo4.findByTelephoneDetails(t1DTO.number) } answers {
            t1
        }
        every { repo4.save(any()) } answers { t1 }

        val service = ContactServiceImpl(mockk(), mockk(), mockk(), repo4, mockk(), mockk(), mockk(), mockk(), mockk())

        // Act

        val res = service.findOrCreateTelephone(t1DTO.number)

        // Assert
        assert(res.id == t1.id)
        verify(exactly = 0) { repo4.save(any()) }
    }

    @Test
    fun `it should return the new Telephone created`() {
        // Arrange
        every { repo4.findByTelephoneDetails(t1DTO.number) } answers {
            null
        }
        every { repo4.save(any()) } answers { t1 }

        val service = ContactServiceImpl(mockk(), mockk(), mockk(), repo4, mockk(), mockk(), mockk(), mockk(), mockk())

        // Act

        val res = service.findOrCreateTelephone(t1DTO.number)

        // Assert
        assert(res.number == t1DTO.number)
        verify { repo4.save(any()) }
    }

    // updateContact

    @Test
    fun `it should correctly update a contact`() {
        // Arrange
        every { repo1.findById(1) } answers { Optional.of(c3) }
        every { repo1.save(any()) } answers { c1 }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        // Act

        val res = service.updateContact(1, null, c3DTO)

        // Assert
        if (res != null) {
            assert(res.name == c3DTO.name)
            assert(res.surname == c3DTO.surname)
            assert(res.category == c3DTO.category)
            assert(res.ssncode == c3DTO.ssncode)
        }
        verify { repo1.save(any()) }
    }

    @Test
    fun `it should correctly update a customer`() {
        // Arrange
        every { repo1.findById(1) } answers { Optional.of(c1) }
        every { repo1.save(any()) } answers { c1 }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        // Act

        val res = service.updateContact(1, CategoryType.CUSTOMER, c1DTO)

        // Assert
        if (res != null) {
            assert(res.name == c1DTO.name)
            assert(res.surname == c1DTO.surname)
            assert(res.category == c1DTO.category)
            assert(res.ssncode == c1DTO.ssncode)
        }
        verify { repo1.save(any()) }
    }

    @Test
    fun `it should correctly update a professional`() {
        // Arrange
        every { repo1.findById(1) } answers {
            c2.professional = p1
            Optional.of(c2)
        }
        every { repo1.save(any()) } answers { c1 }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        // Act

        val res = service.updateContact(1, CategoryType.PROFESSIONAL, c2DTO)

        // Assert
        if (res != null) {
            assert(res.name == c2DTO.name)
            assert(res.surname == c2DTO.surname)
            assert(res.category == c2DTO.category)
            assert(res.ssncode == c2DTO.ssncode)
            assert(res.professionalInfo != null)
        }
        verify { repo1.save(any()) }
    }

    @Test
    fun `it should correctly update a professional changing emp state having a job`() {
        // Arrange
        every { repo1.findById(1) } answers {
            c2.professional = p1
            j1.status = JobStatus.CONSOLIDATED
            p1.jobs.add(j1)
            Optional.of(c2) }
        every { repo1.save(any()) } answers { c2 }
        every { repo8.save(any()) } answers { j1 }
        every { repo9.save(any()) } answers { action1 }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), repo8, repo9)

        // Act

        val res = service.updateContact(1, CategoryType.PROFESSIONAL, c4DTO)

        // Assert
        if (res != null) {
            assert(res.name == c2DTO.name)
            assert(res.surname == c2DTO.surname)
            assert(res.category == c2DTO.category)
            assert(res.ssncode == c2DTO.ssncode)
            assert(res.professionalInfo != null)
        }
        verify { repo1.save(any()) }
        verify { repo8.save(any()) }
        verify { repo9.save(any()) }
    }

    @Test
    fun `it should raise an error not finding the contact trying to update it`() {
        // Arrange
        every { repo1.findById(1) } returns Optional.empty()
        every { repo1.save(any()) } answers { c1 }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        // Act & Assert
        assertThrows<ContactNotFoundException> {
            service.updateContact(1, null, c1DTO)
        }
        verify(exactly = 0) { repo1.save(any()) }
    }

    @Test
    fun `it should raise an error not finding professional info trying to update a professional`() {
        // Arrange
        every { repo1.findById(1) } answers {
            c2.professional = p1
            Optional.of(c2)
        }
        every { repo1.save(any()) } answers { c1 }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        // Act & Assert
        assertThrows<InvalidCategoryException> {
            service.updateContact(1, CategoryType.PROFESSIONAL, cErr1)
        }
        verify(exactly = 0) { repo1.save(any()) }
    }

    @Test
    fun `it should raise an error trying to update also contact's category`() {
        // Arrange
        every { repo1.findById(1) } answers {
            c2.professional = p1
            Optional.of(c2)
        }
        every { repo1.save(any()) } answers { c1 }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        // Act & Assert
        assertThrows<InvalidCategoryException> {
            service.updateContact(1, null, c1DTO )
        }
        verify(exactly = 0) { repo1.save(any()) }
    }

    @Test
    fun `it should raise an error finding an invalid emp state trying to update a professional`() {
        // Arrange
        every { repo1.findById(1) } answers {
            c2.professional = p1
            Optional.of(c2)
        }
        every { repo1.save(any()) } answers { c1 }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        // Act & Assert
        assertThrows<InvalidEmpStateException> {
            service.updateContact(1, CategoryType.PROFESSIONAL, cErr2 )
        }
        verify(exactly = 0) { repo1.save(any()) }
    }

    // deleteContact

    @Test
    fun `it should correctly delete the Contact`() {
        // Arrange
        every { repo1.findById(any()) } answers { Optional.of(c3) }
        every { repo1.deleteById(1) } answers {}

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        // Act
        service.deleteContact(1, null)

        // Assert
        verify { repo1.deleteById(1) }
    }

    @Test
    fun `it should correctly delete the Customer`() {
        // Arrange
        every { repo1.findById(any()) } answers {
            c1.customer = cu
            Optional.of(c1) }
        every { repo1.deleteById(1) } answers {}

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        // Act
        service.deleteContact(1, CategoryType.CUSTOMER)

        // Assert
        verify { repo1.deleteById(1) }
    }

    @Test
    fun `it should correctly delete the Professional`() {
        // Arrange
        every { repo1.findById(any()) } answers {
            c2.professional = p1
            Optional.of(c2) }
        every { repo1.deleteById(1) } answers {}

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        // Act
        service.deleteContact(1, CategoryType.PROFESSIONAL)

        // Assert
        verify { repo1.deleteById(1) }
    }

    @Test
    fun `it should raise an error not finding the Contact trying to delete`() {
        // Arrange
        every { repo1.findById(any()) } returns Optional.empty()
        every { repo1.deleteById(1) } answers {}

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        // Act & Assert
        assertThrows<ContactNotFoundException> {
            service.deleteContact(1, null)
        }
        verify(exactly = 0) { repo1.deleteById(1) }
    }

    @Test
    fun `it should raise an error finding a Contact that it is not a Customer trying to delete`() {
        // Arrange
        every { repo1.findById(any()) } answers { Optional.of(c2) }
        every { repo1.deleteById(1) } answers {}

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        // Act & Assert
        assertThrows<ContactNotFoundException> {
            service.deleteContact(1, CategoryType.CUSTOMER)
        }
        verify(exactly = 0) { repo1.deleteById(1) }
    }

    @Test
    fun `it should raise an error finding a Contact that it is not a Professional trying to delete`() {
        // Arrange
        every { repo1.findById(any()) } answers { Optional.of(c1) }
        every { repo1.deleteById(1) } answers {}

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk(), mockk())

        // Act & Assert
        assertThrows<ContactNotFoundException> {
            service.deleteContact(1, CategoryType.PROFESSIONAL)
        }
        verify(exactly = 0) { repo1.deleteById(1) }
    }

    // addNotes
    @Test
    fun `it should correctly add notes to a Customer`() {
        // Arrange
        every { repo1.findById(any()) } answers { Optional.of(c1) }
        every { repo1.save(any()) } answers { c1 }
        every { repo7.save(any()) } answers { n1 }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), repo7, mockk(), mockk())

        // Act
        service.addNotes(1, CategoryType.CUSTOMER, n1DTO)

        // Assert
        verify { repo1.save(any()) }
        verify { repo7.save(any()) }
    }

    @Test
    fun `it should correctly add notes to a Professional`() {
        // Arrange
        every { repo1.findById(any()) } answers { Optional.of(c2) }
        every { repo1.save(any()) } answers { c1 }
        every { repo7.save(any()) } answers { n1 }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), repo7, mockk(), mockk())

        // Act
        service.addNotes(1, CategoryType.PROFESSIONAL, n1DTO)

        // Assert
        verify { repo1.save(any()) }
        verify { repo7.save(any()) }
    }

    @Test
    fun `it should raise an error not finding contact trying to add notes`() {
        // Arrange
        every { repo1.findById(any()) } returns Optional.empty()
        every { repo1.save(any()) } answers { c1 }
        every { repo7.save(any()) } answers { n1 }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), repo7, mockk(), mockk())

        // Act
        assertThrows<ContactNotFoundException> {
            service.addNotes(1, CategoryType.CUSTOMER, n1DTO)
        }
        verify(exactly = 0) { repo1.save(any()) }
        verify(exactly = 0) { repo7.save(any()) }
    }

    @Test
    fun `it should raise an error finding a Professional trying to add notes to a Customer`() {
        // Arrange
        every { repo1.findById(any()) } answers { Optional.of(c2) }
        every { repo1.save(any()) } answers { c1 }
        every { repo7.save(any()) } answers { n1 }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), repo7, mockk(), mockk())

        assertThrows<InvalidCategoryException> {
            service.addNotes(1, CategoryType.CUSTOMER, n1DTO)
        }
        verify(exactly = 0) { repo1.save(any()) }
        verify(exactly = 0) { repo7.save(any()) }
    }

    @Test
    fun `it should raise an error finding a Customer trying to add notes to a Professional`() {
        // Arrange
        every { repo1.findById(any()) } answers { Optional.of(c1) }
        every { repo1.save(any()) } answers { c1 }
        every { repo7.save(any()) } answers { n1 }

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), repo7, mockk(), mockk())

        assertThrows<InvalidCategoryException> {
            service.addNotes(1, CategoryType.PROFESSIONAL, n1DTO)
        }
        verify(exactly = 0) { repo1.save(any()) }
        verify(exactly = 0) { repo7.save(any()) }
    }

    //getNotesById

    @Test
    fun `it should correctly retrieve notes to a Customer`() {
        // Arrange
        every { repo1.findById(any()) } answers { Optional.of(c1) }
        every { repo7.getNotesByContactId(1, CategoryType.CUSTOMER)} answers { listOf(n1, n2)}

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), repo7, mockk(), mockk())

        // Act
        val res = service.getNotesById(1, CategoryType.CUSTOMER)

        // Assert
        assert(res.size == 2)
        assert(res[0].note == "testNote1")
        assert(res[1].note == "testNote2")
        verify { repo7.getNotesByContactId(1, CategoryType.CUSTOMER) }
    }

    @Test
    fun `it should correctly retrieve notes to a Professional`() {
        // Arrange
        every { repo1.findById(any()) } answers { Optional.of(c2) }
        every { repo7.getNotesByContactId(1, CategoryType.PROFESSIONAL)} answers { listOf(n1, n2)}

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), repo7, mockk(), mockk())

        // Act
        val res = service.getNotesById(1, CategoryType.PROFESSIONAL)

        // Assert
        assert(res.size == 2)
        assert(res[0].note == "testNote1")
        assert(res[1].note == "testNote2")
        verify { repo7.getNotesByContactId(1, CategoryType.PROFESSIONAL) }
    }

    @Test
    fun `it should raise an error not finding contact trying to retrieve notes`() {
        // Arrange
        every { repo1.findById(any()) } answers { Optional.empty() }
        every { repo7.getNotesByContactId(1, CategoryType.CUSTOMER)} answers { listOf(n1, n2)}

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), repo7, mockk(), mockk())

        // Act
        assertThrows<ContactNotFoundException> {
            service.getNotesById(1, CategoryType.CUSTOMER)
        }
        verify(exactly = 0) { repo7.getNotesByContactId(any(), any()) }
    }

    @Test
    fun `it should raise an error finding a Professional trying to retrieve notes for a Customer`() {
        // Arrange
        every { repo1.findById(any()) } answers { Optional.of(c2) }
        every { repo7.getNotesByContactId(1, CategoryType.CUSTOMER)} answers { listOf(n1, n2)}

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), repo7, mockk(), mockk())

        assertThrows<InvalidCategoryException> {
            service.getNotesById(1, CategoryType.CUSTOMER)
        }
        verify(exactly = 0) { repo7.getNotesByContactId(any(), any()) }
    }

    @Test
    fun `it should raise an error finding a Customer trying to retrieve notes for a Professional`() {
        // Arrange
        every { repo1.findById(any()) } answers { Optional.of(c1) }
        every { repo7.getNotesByContactId(1, CategoryType.PROFESSIONAL)} answers { listOf(n1, n2)}

        val service = ContactServiceImpl(repo1, mockk(), mockk(), mockk(), mockk(), mockk(), repo7, mockk(), mockk())

        assertThrows<InvalidCategoryException> {
            service.getNotesById(1, CategoryType.PROFESSIONAL)
        }
        verify(exactly = 0) { repo7.getNotesByContactId(any(), any()) }
    }
}