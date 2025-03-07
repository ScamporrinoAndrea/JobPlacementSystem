package com.example.communicationmngr

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.apache.kafka.clients.admin.NewTopic


@SpringBootApplication
class CommunicationmngrApplication{
    @Bean
    fun topic1() = NewTopic("emailToStore", 10, 1)

    @Bean
    fun topic2() = NewTopic("emailToSend", 10, 1)
}

fun main(args: Array<String>) {
    runApplication<CommunicationmngrApplication>(*args)
}
