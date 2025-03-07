package it.polito.g21.crm.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.g21.crm.entities.Message
import it.polito.g21.crm.models.ActionOnMessageDTO
import it.polito.g21.crm.models.MessageDTO
import it.polito.g21.crm.models.NewEmailDTO
import it.polito.g21.crm.models.StateWithMessageDTO
import it.polito.g21.crm.services.ContactService
import it.polito.g21.crm.services.MessageService
import it.polito.g21.crm.utils.LoggerConfig
import it.polito.g21.crm.utils.PriorityValue
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.apache.camel.CamelContext
import org.apache.camel.ProducerTemplate
import org.apache.camel.builder.ExchangeBuilder
import org.springframework.http.HttpStatus
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.logging.Logger

@RestController
@RequestMapping("/API/messages")
class MessageController(val messageService : MessageService, private val objectMapper: ObjectMapper,
                        private val kafkaTemplate: KafkaTemplate<String, String>, val logger: LoggerConfig) {
    @GetMapping("/","")
    @PreAuthorize("hasRole('manager') or hasRole('guest') or hasRole('operator')")
    fun getAllMessages(@Valid @RequestParam("pageNumber") @Min(1) pageNumber: Int?,
                       @Valid @RequestParam("limit") @Min(0) limit: Int?,
                       @RequestParam("date") date: LocalDate?,
                       @RequestParam("subject") subject: String?,
                       @RequestParam("body") body: String?,
                       @RequestParam("channel") channel: String?,
                       @RequestParam("state") state: String?,
                       @RequestParam("priority") priority: Int?,
                       @RequestParam("email") email: String?,
                       @RequestParam("city") city: String?,
                       @RequestParam("country") country: String?,
                       @RequestParam("street") street: String?,
                       @RequestParam("telephone") telephone: String?) : Map<String, List<MessageDTO>> {
        return mapOf("messages" to messageService.getMessages(pageNumber, limit, date, subject, body, channel, state, priority, email, city, country, street, telephone))
    }

    @PostMapping("/","")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('manager') or hasRole('operator')")
    fun createMessage(@RequestBody dto :MessageDTO){
        messageService.createMessage(dto)
        logger.info("Message created successfully")
    }

    @GetMapping("{messageId}","{messageId}/")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('manager') or hasRole('guest') or hasRole('operator')")
    fun getMessageById(@PathVariable("messageId") id: Long) : MessageDTO? {
        return messageService.getMessageById(id)
    }

    @PostMapping("{messageId}","{messageId}/")
    @PreAuthorize("hasRole('manager') or hasRole('operator')")
    fun changeState(@PathVariable("messageId") id : Long, @RequestBody dto : StateWithMessageDTO){
        val res = messageService.changeState(id, dto)
        logger.info("Message state changed successfully")
        if(res?.channel == "email"){
            val mail = objectMapper.writeValueAsString(NewEmailDTO(res.sender, "State message modified", "The message has changed its state into ${dto.state}!" ))
            kafkaTemplate.send("emailToSend", mail)
            logger.info("Email sent successfully")
        }
    }

    @GetMapping("{messageId}/history", "{messageId}/history/")
    @PreAuthorize("hasRole('manager') or hasRole('guest') or hasRole('operator')")
    fun getMessageHistory(@PathVariable("messageId") id : Long) : List<ActionOnMessageDTO>{
        return messageService.getHistory(id)
    }

    @PutMapping("{messageId}/priority", "{messageId}/priority/")
    @PreAuthorize("hasRole('manager') or hasRole('operator')")
    fun changePriority(@PathVariable("messageId") id : Long, @RequestParam("value") value : Int){
        messageService.updatePriority(id,value)
        logger.info("Message priority changed successfully")
    }
}