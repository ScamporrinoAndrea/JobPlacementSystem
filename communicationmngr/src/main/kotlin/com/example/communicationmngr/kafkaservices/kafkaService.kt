package com.example.communicationmngr.kafkaservices

import com.example.communicationmngr.controllers.MailController
import com.example.communicationmngr.dtos.NewEmailDTO
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.camel.CamelContext
import org.apache.camel.ProducerTemplate
import org.apache.camel.builder.ExchangeBuilder
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import java.util.logging.Logger

interface KafkaService {
    fun listen(value: String)
}

@Service
class KafkaServiceImpl(val camelContext: CamelContext, val producerTemplate: ProducerTemplate,
                       private val objectMapper : ObjectMapper) : KafkaService {

    @KafkaListener(id = "consumer", topics = ["emailToSend"])
    override fun listen(value: String){
        val mail = objectMapper.readValue<NewEmailDTO>(value)
        val exchange = ExchangeBuilder.anExchange(camelContext)
            .withBody(mail)
            .withHeader("To", mail.recipient)
            .withHeader("Subject", mail.subject)
            .build()

        producerTemplate.send("direct:sendMail", exchange)
        println(mail.body)
    }
}