package it.polito.g21.analytics.kafkaservices

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import it.polito.g21.analytics.LoggerConfig
import it.polito.g21.analytics.entities.Contact
import it.polito.g21.analytics.entities.JobOffer
import it.polito.g21.analytics.models.ContactDTO
import it.polito.g21.analytics.models.JobDTO
import it.polito.g21.analytics.repositories.ContactRepo
import it.polito.g21.analytics.repositories.JobRepo
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

interface KafkaService {
    fun newContact(value: String)

    fun newJob(value: String)
}

@Service
class KafkaServiceImpl(private val objectMapper : ObjectMapper, private val contactRepo : ContactRepo,
                       private val jobRepo: JobRepo, val logger: LoggerConfig) : KafkaService{

    @KafkaListener(id = "consumer1", topics = ["newContact"])
    override fun newContact(value: String) {
        val contact = objectMapper.readValue<ContactDTO>(value)
        contactRepo.save(Contact(null, contact.category, contact.day, contact.month, contact.year))
        logger.info("New contact topic received")
    }

    @KafkaListener(id = "consumer2", topics = ["newJob"])
    override fun newJob(value: String) {
        val job = objectMapper.readValue<JobDTO>(value)
        jobRepo.save(JobOffer(null, job.status, job.id, job.day, job.month, job.year))
        logger.info("New job topic received")
    }

}