package it.polito.g21.crm.unit

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import it.polito.g21.crm.entities.*
import it.polito.g21.crm.exceptionhandler.*
import it.polito.g21.crm.models.*
import it.polito.g21.crm.repositories.*
import it.polito.g21.crm.services.JobOfferServiceImpl
import it.polito.g21.crm.utils.CategoryType
import it.polito.g21.crm.utils.EmploymentState
import it.polito.g21.crm.utils.JobStatus
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime
import java.util.Optional

class JobOfferServiceUnitTest {
    private val repo1 = mockk<JobOfferRepo>()
    private val repo2 = mockk<CustomerRepo>()
    private val repo3 = mockk<ProfessionalRepo>()
    private val repo4 = mockk<ActionOnJobRepo>()

    private val myDateTime = LocalDateTime.now()

    private val c1 = Contact("Mario", "Rossi", CategoryType.CUSTOMER, "123456789")
    private val c2 = Contact("Mario", "Verdi", CategoryType.PROFESSIONAL, "123456788")

    private val cu = Customer(c1)

    private val pi1 = ProfessionalDTO("testSkill", "employed", "testLocation", 4.5, null)

    private val p1 = Professional(pi1.skills, EmploymentState.EMPLOYED, pi1.location, pi1.dailyRate, c2)
    private val p2 = Professional(pi1.skills, EmploymentState.AVAILABLE, pi1.location, pi1.dailyRate, c2)

    private val j1DTO = JobOfferDTO(null, "testDesc", "testSkills", 1, null, 1.2, null, null)
    private val j2DTO = JobOfferDTO(null, "testDesc2", "testSkills2", 2, null, 1.2, null, null)
    private val j1 = JobOffer("testDesc", "testSkills", 1, 1.2, cu)
    private val j2 = JobOffer("testDesc2", "testSkills2", 2, 1.2, cu)

    private val openList = listOf(JobStatus.CREATED, JobStatus.SELECTION_PHASE,  JobStatus.CANDIDATE_PROPOSAL)
    private val abortedList = listOf(JobStatus.ABORTED)
    private val acceptedList = listOf(JobStatus.CONSOLIDATED, JobStatus.DONE)

    private val js1DTO = JobStatusDTO("selection_phase", "testNote", null)
    private val js2DTO = JobStatusDTO("consolidated", "testNote", 2)
    private val js3DTO = JobStatusDTO("done", "testNote", null)
    private val js4DTO = JobStatusDTO("selection_phase", "testNote", null)
    private val jsErr1DTO = JobStatusDTO("error_state", "testNote", null)
    private val jsErr2DTO = JobStatusDTO("consolidated", "testNote", null)


    private val a1 = ActionOnJob(JobStatus.SELECTION_PHASE, myDateTime, "testNote", j1, null)
    private val a2 = ActionOnJob(JobStatus.CANDIDATE_PROPOSAL, myDateTime, "testNote", j1, null)
    private val a3 = ActionOnJob(JobStatus.CONSOLIDATED, myDateTime, "testNote", j1, p1)
    private val a4 = ActionOnJob(JobStatus.ABORTED, myDateTime, "testNote", j2, p1)

    // createJobOffer
    @Test
    fun `it should create a job offer`(){
        // Arrange

        every{
            repo2.findCustomerByContactId(1)
        } answers {
            cu
        }
        every { repo1.save(any()) } answers  { j1 }
        every { repo2.save(any()) } answers  { cu }

        val service = JobOfferServiceImpl(repo1, repo2, mockk(), mockk())

        // Act
        service.createJobOffer(j1DTO, 1)


        // Assert
        verify { repo1.save(any()) }
        verify { repo2.save(any()) }
    }

    @Test
    fun `it should raise an error not finding the customer trying to create a job offer`(){
        // Arrange

        every{
            repo2.findCustomerByContactId(1)
        } answers {
            null
        }
        every { repo1.save(any()) } answers  { j1 }
        every { repo2.save(any()) } answers  { cu }

        val service = JobOfferServiceImpl(repo1, repo2, mockk(), mockk())

        // Act & Assert
        assertThrows<ContactNotFoundException> {
            service.createJobOffer(j1DTO, 1)
        }
        verify(exactly = 0) { repo1.save(any()) }
        verify(exactly = 0) { repo2.save(any()) }
    }

    // getJobOfferById
    @Test
    fun `it should retrieve a job offer having its id`(){
        // Arrange

        every{
            repo1.findById(1)
        } answers {
            Optional.of(j1)
        }

        val service = JobOfferServiceImpl(repo1, mockk(), mockk(), mockk())

        // Act
        val res = service.getJobOfferById(1)

        // Assert
        if (res != null) {
            assert(res.description == j1.description)
            assert(res.requiredSkills == j1.requiredSkills)
            assert(res.duration == j1.duration)
            assert(res.profitMargin == j1.profitMargin)
        }
    }

    @Test
    fun `it should raise an error not finding the job offer trying to retrieve it`(){
        // Arrange

        every{
            repo1.findById(1)
        } answers {
            Optional.empty()
        }

        val service = JobOfferServiceImpl(repo1, repo2, mockk(), mockk())

        // Act & Assert
        assertThrows<JobOfferNotFoundException> {
            service.getJobOfferById(1)
        }
    }

    // getJobOffersByStatusAndId
    @Test
    fun `it should retrieve all open job Offers filtering customers`(){
        // Arrange

        every{
            repo1.findJobOffersByStatusAndContactId(1, openList, CategoryType.CUSTOMER, null)
        } answers {
            listOf(j1, j2)
        }

        val service = JobOfferServiceImpl(repo1, mockk(), mockk(), mockk())

        // Act
        val res = service.getJobOffersByStatusAndId(1, "customer", "open", null, null)

        // Assert
        assert(res.size == 2)
        assert(res[0].description == j1.description)
        assert(res[0].requiredSkills == j1.requiredSkills)
        assert(res[0].duration == j1.duration)
        assert(res[0].profitMargin == j1.profitMargin)
        assert(res[1].description == j2.description)
        assert(res[1].requiredSkills == j2.requiredSkills)
        assert(res[1].duration == j2.duration)
        assert(res[1].profitMargin == j2.profitMargin)
    }

    @Test
    fun `it should retrieve all open job Offers filtering customers with pagination`(){
        // Arrange

        every{
            repo1.findJobOffersByStatusAndContactId(1, openList, CategoryType.CUSTOMER, PageRequest.of(0, 2))
        } answers {
            listOf(j1, j2)
        }

        val service = JobOfferServiceImpl(repo1, mockk(), mockk(), mockk())

        // Act
        val res = service.getJobOffersByStatusAndId(1, "customer", "open", 1, 2)

        // Assert
        assert(res.size == 2)
        assert(res[0].description == j1.description)
        assert(res[0].requiredSkills == j1.requiredSkills)
        assert(res[0].duration == j1.duration)
        assert(res[0].profitMargin == j1.profitMargin)
        assert(res[1].description == j2.description)
        assert(res[1].requiredSkills == j2.requiredSkills)
        assert(res[1].duration == j2.duration)
        assert(res[1].profitMargin == j2.profitMargin)
    }

    @Test
    fun `it should retrieve all accepted job Offers filtering professional`(){
        // Arrange

        every{
            repo1.findJobOffersByStatusAndContactId(1, acceptedList, CategoryType.PROFESSIONAL, null)
        } answers {
            listOf(j1, j2)
        }

        val service = JobOfferServiceImpl(repo1, mockk(), mockk(), mockk())

        // Act
        val res = service.getJobOffersByStatusAndId(1, "professional", "accepted", null, null)


        // Assert
        assert(res.size == 2)
        assert(res[0].description == j1.description)
        assert(res[0].requiredSkills == j1.requiredSkills)
        assert(res[0].duration == j1.duration)
        assert(res[0].profitMargin == j1.profitMargin)
        assert(res[1].description == j2.description)
        assert(res[1].requiredSkills == j2.requiredSkills)
        assert(res[1].duration == j2.duration)
        assert(res[1].profitMargin == j2.profitMargin)

    }

    @Test
    fun `it should retrieve all aborted job Offers filtering professional`(){
        // Arrange

        every{
            repo1.findJobOffersByStatusAndContactId(1, abortedList, CategoryType.PROFESSIONAL, null)
        } answers {
            listOf(j1, j2)
        }

        val service = JobOfferServiceImpl(repo1, mockk(), mockk(), mockk())

        // Act
        val res = service.getJobOffersByStatusAndId(1, "professional", "aborted", null, null)


        // Assert
        assert(res.size == 2)
        assert(res[0].description == j1.description)
        assert(res[0].requiredSkills == j1.requiredSkills)
        assert(res[0].duration == j1.duration)
        assert(res[0].profitMargin == j1.profitMargin)
        assert(res[1].description == j2.description)
        assert(res[1].requiredSkills == j2.requiredSkills)
        assert(res[1].duration == j2.duration)
        assert(res[1].profitMargin == j2.profitMargin)
    }

    @Test
    fun `it should raise an error when category is neither 'customer' nor 'professional' trying to retrieve job offers`(){
        // Arrange

        every{
            repo1.findJobOffersByStatusAndContactId(any(), any(), any(), null)
        } answers {
            listOf(j1, j2)
        }

        val service = JobOfferServiceImpl(repo1, mockk(), mockk(), mockk())

        // Act & Assert
        assertThrows<InvalidCategoryException> {
            service.getJobOffersByStatusAndId(1, "error", "open", null, null)
        }
        verify(exactly = 0) { repo1.findJobOffersByStatusAndContactId(any(), any(), any(), null) }
    }

    @Test
    fun `it should raise an error when 'contactId' or 'category' are not both present or absent trying to retrieve job offers`(){
        // Arrange

        every{
            repo1.findJobOffersByStatusAndContactId(any(), any(), any(), null)
        } answers {
            listOf(j1, j2)
        }

        val service = JobOfferServiceImpl(repo1, mockk(), mockk(), mockk())

        // Act & Assert
        assertThrows<InvalidFilterParamsException> {
            service.getJobOffersByStatusAndId(1, null, "open", null, null)
        }
        verify(exactly = 0) { repo1.findJobOffersByStatusAndContactId(any(), any(), any(), null) }
    }

    @Test
    fun `it should raise an error when status is invalid trying to retrieve job offers`(){
        // Arrange

        every{
            repo1.findJobOffersByStatusAndContactId(any(), any(), any(), null)
        } answers {
            listOf(j1, j2)
        }

        val service = JobOfferServiceImpl(repo1, mockk(), mockk(), mockk())

        // Act & Assert
        assertThrows<InvalidStateException> {
            service.getJobOffersByStatusAndId(1, "customer", "error", null, null)
        }
        verify(exactly = 0) { repo1.findJobOffersByStatusAndContactId(any(), any(), any(), null) }
    }

    @Test
    fun `it should raise an error trying to filter for professionalId on open job offers`(){
        // Arrange

        every{
            repo1.findJobOffersByStatusAndContactId(any(), any(), any(), null)
        } answers {
            listOf(j1, j2)
        }

        val service = JobOfferServiceImpl(repo1, mockk(), mockk(), mockk())

        // Act & Assert
        assertThrows<InvalidFilterParamsException> {
            service.getJobOffersByStatusAndId(1, "professional", "open", null, null)
        }
        verify(exactly = 0) { repo1.findJobOffersByStatusAndContactId(any(), any(), any(), null) }
    }

    // updateJobOfferStatus

    @Test
    fun `it should change job offer status`(){
        // Arrange

        every{ repo1.findById(1) } answers { Optional.of(j1) }
        every{ repo3.findProfessionalByContactId(2) } answers { p2 }
        every{ repo1.save(any()) } answers { j1 }
        every{repo3.save(any()) } answers { p1 }
        every{ repo4.save(any()) } answers { a1 }

        val service = JobOfferServiceImpl(repo1, mockk(), repo3, repo4)

        // Act
        val res = service.updateJobOfferStatus(1, js1DTO)

        // Assert
        assert(res.status == "selection_phase")
        verify { repo1.save(any()) }
        verify(exactly = 0) { repo3.save(any()) }
        verify { repo4.save(any()) }
    }

    @Test
    fun `it should change job offer status and attach a professional`(){
        // Arrange

        every{ repo1.findById(1) } answers {
            j1.status = JobStatus.CANDIDATE_PROPOSAL
            Optional.of(j1)
        }
        every{ repo3.findProfessionalByContactId(2) } answers { p2 }
        every{ repo1.save(any()) } answers { j1 }
        every{repo3.save(any()) } answers { p1 }
        every{ repo4.save(any()) } answers { a1 }

        val service = JobOfferServiceImpl(repo1, mockk(), repo3, repo4)

        // Act
        val res = service.updateJobOfferStatus(1, js2DTO)

        // Assert
        assert(res.status == "consolidated")
        verify { repo1.save(any()) }
        verify { repo3.save(any()) }
        verify { repo4.save(any()) }
    }

    @Test
    fun `it should change job offer status and professional availability when done`(){
        // Arrange

        every{ repo1.findById(1) } answers {
            j1.status = JobStatus.CONSOLIDATED
            j1.professional = p2
            Optional.of(j1)
        }
        every{ repo3.findProfessionalByContactId(2) } answers { p2 }
        every{ repo1.save(any()) } answers { j1 }
        every{repo3.save(any()) } answers { p1 }
        every{ repo4.save(any()) } answers { a1 }

        val service = JobOfferServiceImpl(repo1, mockk(), repo3, repo4)

        // Act
        val res = service.updateJobOfferStatus(1, js3DTO)

        // Assert
        assert(res.status == "done")
        verify { repo1.save(any()) }
        verify { repo3.save(any()) }
        verify { repo4.save(any()) }
    }

    @Test
    fun `it should change job offer status and delete professional's link`(){
        // Arrange

        every{ repo1.findById(1) } answers {
            j1.status = JobStatus.CONSOLIDATED
            j1.professional = p2
            Optional.of(j1)
        }
        every{ repo3.findProfessionalByContactId(2) } answers { p2 }
        every{ repo1.save(any()) } answers { j1 }
        every{repo3.save(any()) } answers { p1 }
        every{ repo4.save(any()) } answers { a1 }

        val service = JobOfferServiceImpl(repo1, mockk(), repo3, repo4)

        // Act
        val res = service.updateJobOfferStatus(1, js4DTO)

        // Assert
        assert(res.status == "selection_phase")
        assert(res.professionalId == null)
        verify { repo1.save(any()) }
        verify { repo3.save(any()) }
        verify { repo4.save(any()) }
    }

    @Test
    fun `it should raise an error not finding the job_offer trying to update it`(){
        // Arrange

        every{ repo1.findById(1) } answers { Optional.empty() }
        every{ repo3.findProfessionalByContactId(2) } answers { p2 }
        every{ repo1.save(any()) } answers { j1 }
        every{repo3.save(any()) } answers { p1 }
        every{ repo4.save(any()) } answers { a1 }

        val service = JobOfferServiceImpl(repo1, mockk(), repo3, repo4)

        // Act & Assert
        assertThrows<JobOfferNotFoundException> {
            service.updateJobOfferStatus(1, js1DTO)
        }
        verify(exactly = 0) { repo1.save(any()) }
        verify(exactly = 0) { repo3.save(any()) }
        verify(exactly = 0) { repo4.save(any()) }
    }

    @Test
    fun `it should raise an error having an invalid job offer state trying to change it`(){
        // Arrange

        every{ repo1.findById(1) } answers { Optional.of(j1) }
        every{ repo3.findProfessionalByContactId(2) } answers { p2 }
        every{ repo1.save(any()) } answers { j1 }
        every{repo3.save(any()) } answers { p1 }
        every{ repo4.save(any()) } answers { a1 }

        val service = JobOfferServiceImpl(repo1, mockk(), repo3, repo4)

        // Act & Assert
        assertThrows<InvalidStateException> {
            service.updateJobOfferStatus(1, jsErr1DTO)
        }
        verify(exactly = 0) { repo1.save(any()) }
        verify(exactly = 0) { repo3.save(any()) }
        verify(exactly = 0) { repo4.save(any()) }
    }

    @Test
    fun `it should raise an error trying to update job offer's state into an invalid one`(){
        // Arrange

        every{ repo1.findById(1) } answers {
            Optional.of(j1) }
        every{ repo3.findProfessionalByContactId(2) } answers { p2 }
        every{ repo1.save(any()) } answers { j1 }
        every{repo3.save(any()) } answers { p1 }
        every{ repo4.save(any()) } answers { a1 }

        val service = JobOfferServiceImpl(repo1, mockk(), repo3, repo4)

        // Act & Assert
        assertThrows<InvalidJobOfferFlowException> {
            service.updateJobOfferStatus(1, js2DTO)
        }
        verify(exactly = 0) { repo1.save(any()) }
        verify(exactly = 0) { repo3.save(any()) }
        verify(exactly = 0) { repo4.save(any()) }
    }

    @Test
    fun `it should raise an error trying to update job offer into consolidated state without any professional provided`(){
        // Arrange

        every{ repo1.findById(1) } answers {
            j1.status = JobStatus.CANDIDATE_PROPOSAL
            Optional.of(j1) }
        every{ repo3.findProfessionalByContactId(2) } answers { p2 }
        every{ repo1.save(any()) } answers { j1 }
        every{repo3.save(any()) } answers { p1 }
        every{ repo4.save(any()) } answers { a1 }

        val service = JobOfferServiceImpl(repo1, mockk(), repo3, repo4)

        // Act & Assert
        assertThrows<InvalidJobOfferFlowException> {
            service.updateJobOfferStatus(1, jsErr2DTO)
        }
        verify(exactly = 0) { repo1.save(any()) }
        verify(exactly = 0) { repo3.save(any()) }
        verify(exactly = 0) { repo4.save(any()) }
    }

    @Test
    fun `it should raise an error trying to update job offer into consolidated state not finding the professional provided`(){
        // Arrange

        every{ repo1.findById(1) } answers {
            j1.status = JobStatus.CANDIDATE_PROPOSAL
            Optional.of(j1) }
        every{ repo3.findProfessionalByContactId(2) } answers { null }
        every{ repo1.save(any()) } answers { j1 }
        every{repo3.save(any()) } answers { p1 }
        every{ repo4.save(any()) } answers { a1 }

        val service = JobOfferServiceImpl(repo1, mockk(), repo3, repo4)

        // Act & Assert
        assertThrows<ContactNotFoundException> {
            service.updateJobOfferStatus(1, js2DTO)
        }
        verify(exactly = 0) { repo1.save(any()) }
        verify(exactly = 0) { repo3.save(any()) }
        verify(exactly = 0) { repo4.save(any()) }
    }

    // computeJobOfferValue

    @Test
    fun `it should compute the job offer's value`(){
        // Arrange

        every{ repo1.findById(1) } answers {
            j1.status = JobStatus.CONSOLIDATED
            j1.professional = p2
            Optional.of(j1)
        }

        val service = JobOfferServiceImpl(repo1, mockk(), mockk(), mockk())

        // Act
        val res = service.computeJobOfferValue(1)

        val normalizedRes = res.replace(".", ",")

        // Assert
        assert(normalizedRes == "5,40")
    }
    @Test
    fun `it should raise an error not finding the job offer trying to compute its value`(){
        // Arrange

        every{ repo1.findById(1) } answers { Optional.empty()}

        val service = JobOfferServiceImpl(repo1, mockk(), mockk(), mockk())

        // Act & Assert
        assertThrows<JobOfferNotFoundException> {
            service.computeJobOfferValue(1)
        }
    }

    @Test
    fun `it should raise an error finding a job not having a professional attached`(){
        // Arrange

        every{ repo1.findById(1) } answers { Optional.of(j1)}

        val service = JobOfferServiceImpl(repo1, mockk(), mockk(), mockk())

        // Act & Assert
        assertThrows<InvalidJobOfferFlowException> {
            service.computeJobOfferValue(1)
        }
    }

    // getJobHistory

    @Test
    fun `it should retrieve history for a given job offer`(){
        // Arrange
        every { repo1.findById(1) } answers { Optional.of(j1) }
        every { repo4.findByJobId(1) } answers { listOf(a1, a2, a3) }

        val service = JobOfferServiceImpl(repo1, mockk(), mockk(), repo4)

        // Act
        val res = service.getJobHistory(1)

        // Assert
        assert(res.size == 3)
        assert(res[0].state == a1.state)
        assert(res[1].state == a2.state)
        assert(res[2].state == a3.state)
    }

    // getCustomerHistory

    @Test
    fun `it should retrieve history for a given customer`(){
        // Arrange
        every { repo2.findCustomerByContactId(1) } answers {cu}
        every{ repo4.findByCustomerId(1) } answers { listOf(a1, a2, a3, a4) }

        val service = JobOfferServiceImpl(mockk(), repo2, mockk(), repo4)

        // Act
        val res = service.getCustomerHistory(1)

        // Assert
        assert(res.size == 4)
        assert(res[0].state == a1.state)
        assert(res[1].state == a2.state)
        assert(res[2].state == a3.state)
        assert(res[3].state == a4.state)
    }

    // getProfessionalHistory

    @Test
    fun `it should retrieve history for a given professional`(){
        // Arrange
        every { repo3.findProfessionalByContactId(1) } answers {p1}
        every{ repo4.findByProfessionalId(1) } answers { listOf(a3, a4) }

        val service = JobOfferServiceImpl(mockk(), mockk(), repo3, repo4)

        // Act
        val res = service.getProfessionalHistory(1)

        // Assert
        assert(res.size == 2)
        assert(res[0].state == a3.state)
        assert(res[1].state == a4.state)
    }

    // updateJobOffer

    @Test
    fun `it should correctly update job offer's information`(){
        // Arrange

        every{ repo1.findById(1) } answers { Optional.of(j1) }
        every {repo1.save(any())} answers { j1 }

        val service = JobOfferServiceImpl(repo1, mockk(), mockk(), mockk())

        // Act
        val res = service.updateJobOffer(1, j2DTO)

        // Assert
        assert(res.description == j2DTO.description)
        assert(res.requiredSkills == j2DTO.requiredSkills)
        assert(res.duration == j2DTO.duration)
        assert(res.profitMargin == j2DTO.profitMargin)
    }

    @Test
    fun `it should raise an error trying to update job offer's information not finding it`(){
        // Arrange

        every{ repo1.findById(1) } answers {Optional.empty()}
        every {repo1.save(any())} answers { j1 }

        val service = JobOfferServiceImpl(repo1, mockk(), repo3, repo4)

        // Act & Assert
        assertThrows<JobOfferNotFoundException> {
            service.updateJobOffer(1, j2DTO)
        }
        verify(exactly = 0) { repo1.save(any()) }
    }
}
