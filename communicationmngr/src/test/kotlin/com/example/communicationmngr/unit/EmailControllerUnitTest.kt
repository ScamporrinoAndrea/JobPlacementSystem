package com.example.communicationmngr.unit

import com.example.communicationmngr.EMailRoute
import com.example.communicationmngr.controllers.MailController
import com.example.communicationmngr.dtos.MessageDTO
import com.example.communicationmngr.dtos.NewEmailDTO
import com.example.communicationmngr.exceptionhandler.InvalidEmailException
import com.example.communicationmngr.exceptionhandler.InvalidSubjectException
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.api.services.gmail.model.Message
import com.google.api.services.gmail.model.MessagePartHeader
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import junit.framework.TestCase
import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.ProducerTemplate
import org.apache.camel.builder.ExchangeBuilder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

internal class MailControllerTest {

    private lateinit var camelContext: CamelContext
    private lateinit var producerTemplate: ProducerTemplate

    val m1DTO = MessageDTO("sender@test.com", null, "subjectTest", "bodyTest", "email", "received", 1)
    val e1DTO = NewEmailDTO("test@example.com", "testSubject", "testBody")
    val e1ErrDTO = NewEmailDTO("invalid-email", "Subject", "Body")
    val e2ErrDTO = NewEmailDTO("test@example.com", "", "Body")

    @BeforeEach
    fun setUp() {
        camelContext = mockk(relaxed = true)
        producerTemplate = mockk()

        every { camelContext.typeConverter } returns mockk()
    }

    @Test
    fun `sendMail with valid data should send email`() {
        val exchange = ExchangeBuilder.anExchange(camelContext)
            .withBody(e1DTO)
            .withHeader("To", e1DTO.recipient)
            .withHeader("Subject", e1DTO.subject)
            .build()

        every { producerTemplate.send(any<String>(), any<Exchange>()) } answers {
            firstArg<String>()
            secondArg<Exchange>().apply {
                `in`.messageId = "mockMessageId"
            }
        }

        val controller = MailController(camelContext, producerTemplate)
        val result = controller.sendMail(e1DTO)

        verify { producerTemplate.send("direct:sendMail", any<Exchange>()) }
        TestCase.assertEquals("mockMessageId", result)
    }

    @Test
    fun `sendMail with invalid email should throw InvalidEmailException`() {
        val controller = MailController(camelContext, producerTemplate)

        assertThrows<InvalidEmailException> {
            controller.sendMail(e1ErrDTO)
        }
    }

    @Test
    fun `it should raise an invalid subject error`() {
        val controller = MailController(camelContext, producerTemplate)

        assertThrows<InvalidSubjectException> {
            controller.sendMail(e2ErrDTO)
        }
    }

    @Test
    fun `it should create a mail with its context`() {
        val email = mockk<MimeMessage>()

        every { email.setFrom(any<InternetAddress>()) } returns Unit
        every { email.addRecipient(any(), any()) } returns Unit
        every { email.subject = any() } returns Unit
        every { email.setContent(any()) } returns Unit

        val component = EMailRoute(mockk(), mockk())
        val result = component.createEmailContent(e1DTO)

        assert(result.session != null)
        assert(result.allHeaders.toList().isNotEmpty())
        assert(result.subject == e1DTO.subject)
    }

    @Test
    fun `it should create email's message`() {
        val mimeMessage = mockk<MimeMessage>()
        val byteArrayOutputStream = java.io.ByteArrayOutputStream()
        every { mimeMessage.writeTo(any()) } answers { args[0] as java.io.OutputStream }

        val component = EMailRoute(mockk(), mockk())
        val encodedMessage = component.createMessageWithEmail(mimeMessage)

        // Verify the result
        val expectedEncodedMessage = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray())
        TestCase.assertEquals(expectedEncodedMessage, encodedMessage)
    }

    @Test
    fun `it should handle the mail correctly`(){
        val exchange = mockk<Exchange>()
        val message = mockk<Message>()
        val om = mockk<ObjectMapper>()
        val mp1 = MessagePartHeader()
        mp1.name = "subject"
        mp1.value = "subjectTest"
        val mp2 = MessagePartHeader()
        mp2.name = "from"
        mp2.value = "sender@test.com"
        val headers = listOf(
            mp1,
            mp2
        )

        // Setting up the payload
        every { message.payload.headers } answers { headers }
        every { exchange.`in`.body } returns m1DTO.body
        every { om.writeValueAsString(m1DTO)} answers { "bodyTest"}

        val component = EMailRoute(om, mockk())
        val resultJson = component.createMessageBody(exchange, message)

        assert(m1DTO.body == resultJson)
    }
}
