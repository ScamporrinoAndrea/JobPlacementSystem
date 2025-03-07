package it.polito.g21.crm.integration

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.PostgreSQLContainer


@SpringBootTest
@ContextConfiguration(initializers = [IntegrationTest.Initializer::class])
abstract class IntegrationTest {

    companion object {
        private val db = PostgreSQLContainer("postgres:latest")
    }

    internal class Initializer: ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(applicationContext: ConfigurableApplicationContext) {
            db.withInitScript("./schema.sql").start()
            TestPropertyValues.of(
                "spring.datasource.url=${db.jdbcUrl}",
                "spring.datasource.username=${db.username}",
                "spring.datasource.password=${db.password}"
            ).applyTo(applicationContext.environment)

        }
    }
}