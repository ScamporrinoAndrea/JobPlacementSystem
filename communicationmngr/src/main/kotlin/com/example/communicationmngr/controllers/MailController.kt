package com.example.communicationmngr.controllers


import com.example.communicationmngr.dtos.NewEmailDTO
import com.example.communicationmngr.exceptionhandler.InvalidEmailException
import com.example.communicationmngr.exceptionhandler.InvalidSubjectException
import org.apache.camel.CamelContext
import org.apache.camel.ProducerTemplate
import org.apache.camel.builder.ExchangeBuilder
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.logging.Logger

@RestController
@RequestMapping("/API/emails/")
class MailController(val camelContext: CamelContext, val producerTemplate: ProducerTemplate) {
    val logger: Logger = Logger.getLogger("MailController")

    @PostMapping("/", "")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('manager') or hasRole('operator')")
    fun sendMail(@RequestBody newEmail: NewEmailDTO) : String {

        //check mail format
        if(!Regex("[a-zA-Z0-9.]+@[a-zA-Z0-9.]+\\.[a-zA-Z]{2,}").matches(newEmail.recipient))
            throw InvalidEmailException("The email provided is not in a valid format")

        //check subject format
        if(newEmail.subject.trim() == "")
            throw InvalidSubjectException("The subject provided is not valid")


        val exchange = ExchangeBuilder.anExchange(camelContext)
            .withBody(newEmail)
            .withHeader("To", newEmail.recipient)
            .withHeader("Subject", newEmail.subject)
            .build()

        producerTemplate.send("direct:sendMail", exchange)

        logger.info("Email sent successfully")
        return exchange.getIn().messageId
    }

}