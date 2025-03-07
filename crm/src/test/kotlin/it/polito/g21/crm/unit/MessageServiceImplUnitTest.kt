package it.polito.g21.crm.unit

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import it.polito.g21.crm.entities.*
import it.polito.g21.crm.exceptionhandler.*
import it.polito.g21.crm.models.*
import it.polito.g21.crm.repositories.*
import it.polito.g21.crm.services.MessageServiceImpl
import it.polito.g21.crm.utils.CategoryType
import it.polito.g21.crm.utils.ChannelType
import it.polito.g21.crm.utils.MachineStateType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageRequest
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

internal class MessageServiceImplUnitTest {
    private val repo1 = mockk<MessageRepo>()
    private val repo2 = mockk<EmailRepo>()
    private val repo3 = mockk<AddressRepo>()
    private val repo4 = mockk<TelephoneRepo>()
    private val repo5 = mockk<ActionOnMessageRepo>()

    private val c1 = Contact("Mario", "Rossi", CategoryType.CUSTOMER, "123456789")

    private val m1 = Email(1, "test@test.com")
    private val a1 = Address(1,  "1", "testName", "testCity", "testCountry")
    private val t1 = Telephone(1, "1234567890")

    private val m1DTO = EmailDTO(null, "test@test.com")
    private val a1DTO = AddressDTO(null, "testName, 1, testCity, testCountry")
    private val t1DTO = TelephoneDTO(null, "1234567890")

    private val myDate = LocalDate.now()
    private val myDateTime = LocalDateTime.now()

    private val mex1 = Message(t1, null, null, myDate, "subjectTest", "bodyTest", ChannelType.PHONE_CALL)
    private val mex2 = Message(null, a1, null, myDate, "subjectTest", "bodyTest", ChannelType.ADDRESS)
    private val mex3 = Message(null, null, m1, myDate, "subjectTest", "bodyTest", ChannelType.EMAIL)

    private val mex1DTO = MessageDTO(null, t1DTO.number, myDate, "subjectTest", "bodyTest", "phone call", "received", null)
    private val mex2DTO = MessageDTO(null, a1DTO.address, myDate, "subjectTest", "bodyTest", "address", null, null)
    private val mex3DTO = MessageDTO(null, m1DTO.mail, myDate, "subjectTest", "bodyTest", "email", null, null)
    private val errMexDTO = MessageDTO(null, a1DTO.address, myDate, "subjectTest", "bodyTest", "phone call", null, null)

    private val stateDTO = StateWithMessageDTO("read", "test comment")

    private val action1 = ActionOnMessage(1, MachineStateType.PROCESSING, myDateTime, mex3, "test comment")

    // createMessage
    @Test
    fun `it should create a message entity with telephone sender`() {
        // Arrange

        every { repo1.save(any()) } answers { mex1 }
        every { repo4.findByTelephoneDetails(t1DTO.number)} answers { t1 }
        every { repo4.save(any())} answers { t1 }
        every { repo3.findByAddressDetails(a1.streetName, a1.streetNumber, a1.city, a1.country)} answers { a1 }
        every { repo3.save(any())} answers { a1 }
        every { repo2.findByMail(m1DTO.mail)} answers { m1 }
        every { repo2.save(any())} answers { m1 }

        val service = MessageServiceImpl(repo1, repo2, repo3, repo4, mockk())

        // Act
        service.createMessage(mex1DTO)

        // Assert
        verify { repo1.save(any()) }
        verify { repo4.findByTelephoneDetails(t1DTO.number) }
        verify( exactly = 0) { repo3.findByAddressDetails(a1.streetName, a1.streetNumber, a1.city, a1.country) }
        verify(exactly = 0) { repo2.findByMail(m1DTO.mail)}
    }

    @Test
    fun `it should create a message entity with address sender`() {
        // Arrange

        every { repo1.save(any()) } answers { mex2 }
        every { repo4.findByTelephoneDetails(t1DTO.number)} answers { t1 }
        every { repo4.save(any())} answers { t1 }
        every { repo3.findByAddressDetails(a1.streetName, a1.streetNumber, a1.city, a1.country)} answers { a1 }
        every { repo3.save(any())} answers { a1 }
        every { repo2.findByMail(m1DTO.mail)} answers { m1 }
        every { repo2.save(any())} answers { m1 }

        val service = MessageServiceImpl(repo1, repo2, repo3, repo4, mockk())

        // Act
        service.createMessage(mex2DTO)

        // Assert
        verify { repo1.save(any()) }
        verify( exactly = 0) { repo4.findByTelephoneDetails(t1DTO.number) }
        verify { repo3.findByAddressDetails(a1.streetName, a1.streetNumber, a1.city, a1.country) }
        verify(exactly = 0) { repo2.findByMail(m1DTO.mail)}
    }

    @Test
    fun `it should create a message entity with email sender`() {
        // Arrange

        every { repo1.save(any()) } answers { mex3 }
        every { repo4.findByTelephoneDetails(t1DTO.number)} answers { t1 }
        every { repo4.save(any())} answers { t1 }
        every { repo3.findByAddressDetails(a1.streetName, a1.streetNumber, a1.city, a1.country)} answers { a1 }
        every { repo3.save(any())} answers { a1 }
        every { repo2.findByMail(m1DTO.mail)} answers { m1 }
        every { repo2.save(any())} answers { m1 }

        val service = MessageServiceImpl(repo1, repo2, repo3, repo4, mockk())

        // Act
        service.createMessage(mex3DTO)

        // Assert
        verify { repo1.save(any()) }
        verify( exactly = 0) { repo4.findByTelephoneDetails(t1DTO.number) }
        verify( exactly = 0) { repo3.findByAddressDetails(a1.streetName, a1.streetNumber, a1.city, a1.country) }
        verify { repo2.findByMail(m1DTO.mail)}
    }

    @Test
    fun `it should raises an error when channel mismatch creating a new message`() {
        // Arrange

        every { repo1.save(any()) } answers { mex1 }
        every { repo4.findByTelephoneDetails(t1DTO.number)} answers { t1 }
        every { repo4.save(any())} answers { t1 }
        every { repo3.findByAddressDetails(a1.streetName, a1.streetNumber, a1.city, a1.country)} answers { a1 }
        every { repo3.save(any())} answers { a1 }
        every { repo2.findByMail(m1DTO.mail)} answers { m1 }
        every { repo2.save(any())} answers { m1 }

        val service = MessageServiceImpl(repo1, repo2, repo3, repo4, mockk())


        // Act & Assert
        assertThrows<MismatchChannelException> {
            service.createMessage(errMexDTO)
        }
        verify( exactly = 0) { repo1.save(any()) }
        verify( exactly = 0) { repo4.findByTelephoneDetails(t1DTO.number) }
        verify( exactly = 0) { repo3.findByAddressDetails(a1.streetName, a1.streetNumber, a1.city, a1.country) }
        verify(exactly = 0) { repo2.findByMail(m1DTO.mail)}
    }

    // getMessages

    @Test
    fun `it should retrieve a list of message Dto`() {
        // Arrange
        every { repo1.findAllFiltered(null, null, null, null, null, null, null, null, null, null, null, null)} answers {
            mex1.telephoneSender?.contacts?.add(c1)
            mex2.addressSender?.contacts?.add(c1)
            listOf(mex1, mex2)}


        val service = MessageServiceImpl(repo1, mockk(), mockk(), mockk(), mockk())

        // Act
        val res = service.getMessages(null, null, null, null, null, null, null, null, null, null, null, null, null)

        // Assert
        assert(res.size == 2)
        assert(res[0].channel == mex1DTO.channel)
        assert(res[0].sender == t1.number)
        assert(res[0].relatedContacts.isNotEmpty())
        assert(res[0].relatedContacts[0].ssncode == c1.SSNCode)
        assert(res[1].channel == mex2DTO.channel)
        assert(res[1].sender == a1.toString())
        assert(res[1].relatedContacts.isNotEmpty())
        assert(res[1].relatedContacts[0].ssncode == c1.SSNCode)
    }

    @Test
    fun `it should retrieve a list of message Dto, returning Unknown Contact`() {
        // Arrange
        every { repo1.findAllFiltered(null, null, null, null, null, null, null, null, null, null, null, null)} answers {
            mex1.telephoneSender?.contacts?.add(c1)
            listOf(mex1, mex2)}


        val service = MessageServiceImpl(repo1, mockk(), mockk(), mockk(), mockk())

        // Act
        val res = service.getMessages(null, null, null, null, null, null, null, null, null, null, null, null, null)

        // Assert
        assert(res.size == 2)
        assert(res[0].channel == mex1DTO.channel)
        assert(res[0].sender == t1.number)
        assert(res[0].relatedContacts.isNotEmpty())
        assert(res[0].relatedContacts[0].ssncode == c1.SSNCode)
        assert(res[1].channel == mex2DTO.channel)
        assert(res[1].sender == a1.toString())
        assert(res[1].relatedContacts.isNotEmpty())
        assert(res[1].relatedContacts[0].name == "???")
    }

    @Test
    fun `it should retrieve a list of message Dto with filters`() {
        // Arrange
        every { repo1.findAllFiltered(myDate, null, mex1DTO.body, null, MachineStateType.RECEIVED, null, null, null, null, null, null, null)} answers {
            mex1.telephoneSender?.contacts?.add(c1)
            listOf(mex1)}


        val service = MessageServiceImpl(repo1, mockk(), mockk(), mockk(), mockk())

        // Act
        val res = service.getMessages(null, null, myDate, null, mex1DTO.body, null, "received", null, null, null, null, null, null)

        // Assert
        assert(res.size == 1)
        assert(res[0].channel == mex1DTO.channel)
        assert(res[0].sender == t1.number)
        assert(res[0].body == mex1.body)
        assert(res[0].date == myDate)
        assert(res[0].state == mex1DTO.state)
        assert(res[0].relatedContacts.isNotEmpty())
        assert(res[0].relatedContacts[0].ssncode == c1.SSNCode)
    }
    @Test
    fun `it should retrieve a list of message Dto with filters and pagination`() {
        // Arrange
        every { repo1.findAllFiltered(myDate, null, mex1DTO.body, null, MachineStateType.RECEIVED, null, null, null, null, null, null, PageRequest.of(0, 2))} answers {
            mex1.telephoneSender?.contacts?.add(c1)
            mex2.addressSender?.contacts?.add(c1)
            listOf(mex1, mex2)}


        val service = MessageServiceImpl(repo1, mockk(), mockk(), mockk(), mockk())

        // Act
        val res = service.getMessages(1, 2, myDate, null, mex1DTO.body, null, "received", null, null, null, null, null, null)

        // Assert
        assert(res.size == 2)
        assert(res[0].channel == mex1DTO.channel)
        assert(res[0].sender == t1.number)
        assert(res[0].body == mex1.body)
        assert(res[0].date == myDate)
        assert(res[0].state == mex1DTO.state)
        assert(res[0].relatedContacts.isNotEmpty())
        assert(res[0].relatedContacts[0].ssncode == c1.SSNCode)
        assert(res[1].channel == mex2DTO.channel)
        assert(res[1].sender == a1.toString())
        assert(res[1].body == mex2.body)
        assert(res[1].date == myDate)
        assert(res[1].state == mex1DTO.state)
        assert(res[1].relatedContacts.isNotEmpty())
        assert(res[1].relatedContacts[0].ssncode == c1.SSNCode)
    }

    @Test
    fun `it should raises an invalid state exception trying to retrieve messages`() {
        // Arrange
        every { repo1.findAllFiltered(null, null, null, null, null, null, null, null, null, null, null, null)} answers {
            listOf()
        }


        val service = MessageServiceImpl(repo1, mockk(), mockk(), mockk(), mockk())

        // Act & Assert
        assertThrows<InvalidStateException> {
            service.getMessages(null, null, null, null, null, null, "errorState", null, null, null, null, null, null)
        }
    }

    @Test
    fun `it should raises an invalid priority exception trying to retrieve messages`() {
        // Arrange
        every { repo1.findAllFiltered(null, null, null, null, null, null, null, null, null, null, null, null)} answers {
            listOf()
        }


        val service = MessageServiceImpl(repo1, mockk(), mockk(), mockk(), mockk())

        // Act & Assert
        assertThrows<InvalidPriorityException> {
            service.getMessages(null, null, null, null, null, null, null, -2, null, null, null, null, null)
        }
    }

    // getMessageById

    @Test
    fun `it should retrieve the message Dto with the given an id`() {
        // Arrange
        every { repo1.findById(1)} answers {
            mex1.telephoneSender?.contacts?.add(c1)
            Optional.of(mex1)
        }


        val service = MessageServiceImpl(repo1, mockk(), mockk(), mockk(), mockk())

        // Act
        val res = service.getMessageById(1)

        // Assert
        assert(res != null)
        if (res != null) {
            assert(res.channel == mex1DTO.channel)
            assert(res.sender == t1.number)
            assert(res.relatedContacts.size == 1)
            assert(res.relatedContacts[0].ssncode == c1.SSNCode)
        }
    }

    @Test
    fun `it should retrieve the message dto with the given id, returning Unknown Contact`() {
        // Arrange
        every { repo1.findById(1)} answers {
            Optional.of(mex1)
        }


        val service = MessageServiceImpl(repo1, mockk(), mockk(), mockk(), mockk())

        // Act
        val res = service.getMessageById(1)

        // Assert
        assert(res != null)
        if (res != null) {
            assert(res.channel == mex1DTO.channel)
            assert(res.sender == t1.number)
            assert(res.relatedContacts.size == 1)
            assert(res.relatedContacts[0].name == "???")
        }
    }

    @Test
    fun `it should raises an error not finding the message with the given id`() {
        // Arrange
        every { repo1.findById(1)} answers {
            Optional.empty()
        }


        val service = MessageServiceImpl(repo1, mockk(), mockk(), mockk(), repo5)

        // Act & Assert
        assertThrows<MessageNotFoundException> {
            service.getMessageById(1)
        }
    }

    // changeState

    @Test
    fun `it should correctly change message's state`() {
        // Arrange
        every { repo1.findById(1)} answers {
            Optional.of(mex3)
        }
        every { repo1.save(any())} answers { mex3 }
        every { repo5.save(any()) } answers { action1 }


        val service = MessageServiceImpl(repo1, mockk(), mockk(), mockk(), repo5)

        // Act
        service.changeState(1, stateDTO)

        // Assert
        verify { repo1.save(any()) }
        verify { repo5.save(any()) }
    }
    @Test
    fun `it should raises an error not finding the message with the given id trying to change its state`() {
        // Arrange
        every { repo1.findById(1)} answers {
            Optional.empty()
        }
        every { repo1.save(any())} answers { mex1 }
        every { repo5.save(any()) } answers { action1 }


        val service = MessageServiceImpl(repo1, mockk(), mockk(), mockk(), repo5)

        // Act & Assert
        assertThrows<MessageNotFoundException> {
            service.changeState(1, stateDTO)
        }
        verify(exactly = 0) { repo1.save(any()) }
        verify(exactly = 0) { repo5.save(any()) }
    }

    @Test
    fun `it should raises an error not finding the message with the given id trying to update state`() {
        // Arrange
        every { repo1.findById(1)} answers {
            mex1.state = MachineStateType.READ
            Optional.of(mex1)
        }
        every { repo1.save(any())} answers { mex1 }
        every { repo5.save(any()) } answers { action1 }


        val service = MessageServiceImpl(repo1, mockk(), mockk(), mockk(), repo5)

        // Act & Assert
        assertThrows<InvalidStateException> {
            service.changeState(1, stateDTO)
        }
        verify(exactly = 0) { repo1.save(any()) }
        verify(exactly = 0) { repo5.save(any()) }
    }

    // getHistory

    @Test
    fun `it should correctly retrieve message's history`() {
        // Arrange
        every { repo1.findById(1)} answers {
            mex1.actions.add(action1)
            Optional.of(mex1)
        }


        val service = MessageServiceImpl(repo1, mockk(), mockk(), mockk(), mockk())

        // Act
        val res = service.getHistory(1)

        // Assert
        assert(res.size == 1)
        assert(res[0].state == action1.state)
        assert(res[0].date == action1.date)
        assert(res[0].comment == action1.comment)
    }
    @Test
    fun `it should raises an error not finding the message with the given id trying to retrieve history`() {
        // Arrange
        every { repo1.findById(1)} answers {
            Optional.empty()
        }


        val service = MessageServiceImpl(repo1, mockk(), mockk(), mockk(), mockk())

        // Act & Assert
        assertThrows<MessageNotFoundException> {
            service.getHistory(1)
        }
    }

    // updatePriority

    @Test
    fun `it should correctly update message's priority`() {
        // Arrange
        every { repo1.findById(1)} answers {
            Optional.of(mex1)
        }
        every { repo1.save(any()) } answers { mex1 }


        val service = MessageServiceImpl(repo1, mockk(), mockk(), mockk(), mockk())

        // Act
        service.updatePriority(1, 2)

        // Assert
        verify { repo1.save(any()) }
    }

    @Test
    fun `it should raises an error not finding the message with the given id trying to update priority`() {
        // Arrange
        every { repo1.findById(1)} answers {
            Optional.empty()
        }
        every { repo1.save(any()) } answers { mex1 }


        val service = MessageServiceImpl(repo1, mockk(), mockk(), mockk(), mockk())

        // Act & Assert
        assertThrows<MessageNotFoundException> {
            service.updatePriority(1, 2)
        }
        verify(exactly = 0) { repo1.save(any()) }
    }

    @Test
    fun `it should raises an invalid priority exception trying to update priority`() {
        // Arrange
        every { repo1.findById(1)} answers {
            Optional.of(mex1)
        }
        every { repo1.save(any()) } answers { mex1 }


        val service = MessageServiceImpl(repo1, mockk(), mockk(), mockk(), mockk())

        // Act & Assert
        assertThrows<InvalidPriorityException> {
            service.updatePriority(1, -2)
        }
        verify(exactly = 0) { repo1.save(any()) }
    }
}