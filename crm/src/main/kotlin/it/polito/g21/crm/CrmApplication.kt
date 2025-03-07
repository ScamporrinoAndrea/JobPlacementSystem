package it.polito.g21.crm

import it.polito.g21.crm.models.MessageDTO
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.kafka.annotation.KafkaListener

@SpringBootApplication
class CrmApplication{
	@Bean
	fun topic1() = NewTopic("emailToStore", 10, 1)

	@Bean
	fun topic2() = NewTopic("emailToSend", 10, 1)

	@Bean
	fun topic3() = NewTopic("newContact", 10, 1)

	@Bean
	fun topic4() = NewTopic("newJob", 10, 1)
}

fun main(args: Array<String>) {
	runApplication<CrmApplication>(*args)
}


