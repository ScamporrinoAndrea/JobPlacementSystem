package it.polito.g21.analytics

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class AnalyticsApplication{
	@Bean
	fun topic3() = NewTopic("newContact", 10, 1)

	@Bean
	fun topic4() = NewTopic("newJob", 10, 1)
}

fun main(args: Array<String>) {
	runApplication<AnalyticsApplication>(*args)
}
