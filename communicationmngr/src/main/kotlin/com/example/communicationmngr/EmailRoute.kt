package com.example.communicationmngr

import com.example.communicationmngr.dtos.MessageDTO
import com.example.communicationmngr.dtos.NewEmailDTO
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.api.services.gmail.model.Message
import jakarta.mail.Session
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeBodyPart
import jakarta.mail.internet.MimeMessage
import jakarta.mail.internet.MimeMultipart
import org.apache.camel.EndpointInject
import org.apache.camel.Exchange
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.google.mail.GoogleMailEndpoint
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.RoutingKafkaTemplate
import org.springframework.stereotype.Component
import java.util.*
import java.util.logging.Logger

@Component
class EMailRoute(private val objectMapper: ObjectMapper, private val kafkaTemplate: KafkaTemplate<String,String>) : RouteBuilder() {
    @EndpointInject("google-mail:messages/get")
    lateinit var ep: GoogleMailEndpoint
    val logger: Logger = Logger.getLogger("CustomerController")

    override fun configure() {
        from("google-mail-stream:0?markAsRead=true&scopes=https://mail.google.com")
            .process {
                val id = it.getIn().getHeader("CamelGoogleMailId").toString()
                logger.info("Email received")
                val message = ep.client.users().messages().get("me", id).execute()
                kafkaTemplate.send("emailToStore", createMessageBody(it, message))
            }





        from("direct:sendMail")
            .process { exchange ->
                val userId = "me" // or the user's email address

                val emailContent = createEmailContent(exchange.getIn().getBody(NewEmailDTO::class.java)) // Function to create email content
                val emailMessage = createMessageWithEmail(emailContent)

                val gmailMessage = Message()
                gmailMessage.raw = emailMessage

                // Send email using Gmail API
                val result = ep.client.users().messages().send(userId, gmailMessage).execute()
                exchange.message.body = result
                logger.info("Email sent successfully")
            }


    }

    fun createMessageBody(exchange: Exchange, message: Message) : String {
        val subject = message.payload.headers.find{it.name.equals("subject",true)}?.get("value")?.toString() ?: ""
        val from = message.payload.headers.find{it.name.equals("from",true)}?.get("value")?.toString() ?: ""
        val emailRegex = Regex("([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})")


        val email = emailRegex.find(from)?.value ?: ""


        val messageDTO = MessageDTO(
            sender = email,
            date = null,
            subject = subject,
            body = exchange.getIn().body.toString(),
            channel = "email",
            state = "received",
            priority = 1 //default value = low
        )

        return objectMapper.writeValueAsString(messageDTO)
    }

    // Function to create a MimeMessage
    fun createEmailContent(dto: NewEmailDTO): MimeMessage {

        //val props = Properties().setProperty("content-type", "multipart/alternative")
        val session = Session.getDefaultInstance(Properties(), null)
        val email = MimeMessage(session)

        email.setFrom(InternetAddress("wa.ii.21.2024@gmail.com"))
        email.addRecipient(jakarta.mail.Message.RecipientType.TO, InternetAddress(dto.recipient))
        email.subject = dto.subject

        // Crea il MimeMultipart per il contenuto alternativo
        val multipart = MimeMultipart("alternative")

        // Crea la parte del testo semplice
        val textPart = MimeBodyPart()
        textPart.setText(dto.body, "utf-8")

        // Crea la parte HTML
        val htmlPart = MimeBodyPart()
        htmlPart.setContent(dto.body, "text/html; charset=utf-8")


        // Aggiungi le parti al Multipart
        multipart.addBodyPart(textPart)
        multipart.addBodyPart(htmlPart)

        // Imposta il contenuto del messaggio
        email.setContent(multipart)


        //email.setText(dto.body)

        return email
    }

    // Function to create a Message object from MimeMessage
    fun createMessageWithEmail(email: MimeMessage): String {
        val bytes = java.io.ByteArrayOutputStream()
        email.writeTo(bytes)
        return Base64.getEncoder().encodeToString(bytes.toByteArray())
    }



}




