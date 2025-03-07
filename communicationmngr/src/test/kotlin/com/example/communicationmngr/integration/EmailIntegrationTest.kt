package com.example.communicationmngr.integration

import com.example.communicationmngr.dtos.NewEmailDTO
import jakarta.annotation.PostConstruct
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.core.io.Resource
import org.springframework.http.*
import org.springframework.jdbc.core.JdbcTemplate

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class EmailIntegrationTest : IntegrationTest(){
    val accessToken = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI4Y3hkcHZoTGVicGdYcmhvRGl5ZjFNNmVDY01UeE42NEt1YnRGUnlIdlB3In0.eyJleHAiOjE3Mjg1NjI2NTYsImlhdCI6MTcxODE5NDY1NiwiYXV0aF90aW1lIjoxNzE4MTk0NjU2LCJqdGkiOiIwODg0Zjc2My1mNTg0LTQ2NjItYjhhMC0xZmI3MTc0YTU0OTEiLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjkwOTAvcmVhbG1zL2NybSIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiI1NmRhMWFkOS1iOGVmLTRhYTItYTQ5My1hNmU0MWVjYzBjYTYiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJjcm1jbGllbnQiLCJub25jZSI6Ik9YMGlMc3RWMHRvZEFUYzhWMzl1a0ItNmxKdnVETzdfbmxORzhXTThvR2siLCJzZXNzaW9uX3N0YXRlIjoiMzRhNTYwYjctZWIxNy00MTY4LWEzZGItNTIwYTg2NDYwYjAzIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vbG9jYWxob3N0OjgwODAiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm1hbmFnZXIiLCJvZmZsaW5lX2FjY2VzcyIsImRlZmF1bHQtcm9sZXMtY3JtIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6Im9wZW5pZCBlbWFpbCBwcm9maWxlIG9mZmxpbmVfYWNjZXNzIiwic2lkIjoiMzRhNTYwYjctZWIxNy00MTY4LWEzZGItNTIwYTg2NDYwYjAzIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJuYW1lIjoiRWRvYXJkbyBNb3JlbGxvIiwicHJlZmVycmVkX3VzZXJuYW1lIjoiZWRvbW9yZWxsbyIsImdpdmVuX25hbWUiOiJFZG9hcmRvIiwiZmFtaWx5X25hbWUiOiJNb3JlbGxvIiwiZW1haWwiOiJlZG9hcmRvLm1vcmVsbG8uMTJAZ21haWwuY29tIn0.g3SU2kw421Gj5zFhjrPKKL-opeCTSuXHb_fXHa6v_Z_SGNd-FBjUz5FF7shT2UrSVzJFD5B2XBV1x9vdfZfqM-iu_YrHtEuWpFvRr4FdEb_dgZfSERZfidhB-jSSoIa66fUcBgppfCVAIrPWYxirRog6GUCjHdZujAOGA7nW-eqai7JomoGlOmwxSE11zXbcJFQcuWu1jMtkAaVTfFSE2VjBq6dfJV2Uuz07qUvYts-oKvv_Abq2oD78D92Z6UZwbkdC5M0N009dnuYZGW1OcA1dSKkvJzUDOo9_bVC4qzmxBbMy8RxYJ8zTd1OjdVhh1u5BLEHhiwM4Z9tii6-GxA"

    val headers = HttpHeaders().apply {
        set("Authorization", "Bearer $accessToken")
    }

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Value("classpath:/sql/data.sql")
    lateinit var dataSql: Resource

    @Value("classpath:/sql/clear.sql")
    lateinit var clearSql: Resource

    @PostConstruct
    fun loadData() {
        jdbcTemplate.execute(clearSql.inputStream.reader().readText())
        jdbcTemplate.execute(dataSql.inputStream.reader().readText())
    }

    @Test
    fun `it should send a mail and create a message`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val e1DTO = NewEmailDTO("wa.ii.21.2024@gmail.com", "testSubject", "testBody")
        val entity = HttpEntity(e1DTO, headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/emails/", HttpMethod.POST, entity, String::class.java)
        println("${response.statusCode}")
        assert(response.statusCode == HttpStatus.CREATED)
    }


    @Test
    fun `sendMail with invalid email should throw InvalidEmailException`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val e1ErrDTO = NewEmailDTO("invalid-email", "Subject", "Body")
        val entity = HttpEntity(e1ErrDTO, headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/emails/", HttpMethod.POST, entity, String::class.java)
        println("${response.statusCode}")
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
    }


    @Test
    fun `it should raise an invalid subject error`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val e2ErrDTO = NewEmailDTO("test@example.com", "", "Body")
        val entity = HttpEntity(e2ErrDTO, headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/emails/", HttpMethod.POST, entity, String::class.java)
        println("${response.statusCode}")
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
    }




}