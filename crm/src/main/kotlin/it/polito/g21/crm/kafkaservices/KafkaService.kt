package it.polito.g21.crm.kafkaservices

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import it.polito.g21.crm.models.MessageDTO
import it.polito.g21.crm.services.MessageService
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service


interface KafkaService {
    fun listen(value: String)
}

@Service
class KafkaServiceImpl(val messageService: MessageService, private val objectMapper : ObjectMapper) : KafkaService{

    @KafkaListener(id = "consumer", topics = ["emailToStore"])
    override fun listen(value: String){
        val message = objectMapper.readValue<MessageDTO>(value)
        messageService.createMessage(message)
        println(message.body)
    }

    fun toJson(dto: MessageDTO): String {
        return objectMapper.writeValueAsString(dto)
    }

    fun fromJson(json: String): MessageDTO {
        return objectMapper.readValue(json)
    }


}