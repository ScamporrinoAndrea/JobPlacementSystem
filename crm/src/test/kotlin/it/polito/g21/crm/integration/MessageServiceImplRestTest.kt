package it.polito.g21.crm.integration

import it.polito.g21.crm.models.*
import jakarta.annotation.PostConstruct
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.core.io.Resource
import org.springframework.http.*
import org.springframework.jdbc.core.JdbcTemplate
import java.time.LocalDate


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class MessageServiceImplRestTest : IntegrationTest() {
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
    fun `it should create a message entity with telephone sender`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val t1DTO = TelephoneDTO(null, "1234567890")
        val myDate = LocalDate.now()
        val message = MessageDTO(null, t1DTO.number, myDate, "subjectTest", "bodyTest", "phone call", "received", null)
        val entity = HttpEntity(message, headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/messages/", HttpMethod.POST, entity, String::class.java)
        assert(response.statusCode == HttpStatus.CREATED)
    }

    @Test
    fun `it should create a message entity with address sender`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val a1DTO = AddressDTO(null, "testName, 1, testCity, testCountry")
        val myDate = LocalDate.now()
        val message = MessageDTO(null, a1DTO.address, myDate, "subjectTest", "bodyTest", "address", null, null)
        val entity = HttpEntity(message, headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/messages/", HttpMethod.POST, entity, String::class.java)
        assert(response.statusCode == HttpStatus.CREATED)
    }

    @Test
    fun `it should create a message entity with email sender`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val m1DTO = EmailDTO(null, "test@test.com")
        val myDate = LocalDate.now()
        val message = MessageDTO(null, m1DTO.mail, myDate, "subjectTest", "bodyTest", "email", null, null)
        val entity = HttpEntity(message, headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/messages/", HttpMethod.POST, entity, String::class.java)
        assert(response.statusCode == HttpStatus.CREATED)
    }

    @Test
    fun `it should raises an error when channel mismatch creating a new message`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val a1DTO = AddressDTO(null, "testName, 1, testCity, testCountry")
        val myDate = LocalDate.now()
        val errMexDTO = MessageDTO(null, a1DTO.address, myDate, "subjectTest", "bodyTest", "phone call", null, null)
        val entity = HttpEntity(errMexDTO, headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/messages/", HttpMethod.POST, entity, String::class.java)
        assert(response.statusCode == HttpStatus.BAD_REQUEST)

    }

    @Test
    fun `it should retrieve a list of message Dto`() {
        val response : ResponseEntity<String> = restTemplate.exchange("/API/messages/", HttpMethod.GET, HttpEntity<String>(headers), String::class.java)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.hasBody())
        assert(response.body?.contains("\"messages\":[{\"id\":1022,\"sender\":\"3243253525\",\"date\":\"2024-05-16\",\"subject\":\"test\",\"body\":\"test\",\"channel\":\"phone call\",\"state\":\"received\",\"priority\":1,\"relatedContacts\":[{\"id\":null,\"name\":\"???\",\"surname\":\"???\",\"category\":\"unknown\",\"ssncode\":null,\"professionalInfo\":null}]},{\"id\":1001,\"sender\":\"Street Test, 12, Turin, Italy\",\"date\":\"2024-05-16\",\"subject\":\"test\",\"body\":\"test\",\"channel\":\"address\",\"state\":\"received\",\"priority\":1,\"relatedContacts\":[{\"id\":null,\"name\":\"???\",\"surname\":\"???\",\"category\":\"unknown\",\"ssncode\":null,\"professionalInfo\":null}]}]") ?: false)

    }

    @Test
    fun `it should retrieve a list of message Dto with filters`() {
        val response : ResponseEntity<String> = restTemplate.exchange("/API/messages/?channel=address", HttpMethod.GET, HttpEntity<String>(headers), String::class.java)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.hasBody())
        assert(response.body?.contains("\"messages\":[{\"id\":1001,\"sender\":\"Street Test, 12, Turin, Italy\",\"date\":\"2024-05-16\",\"subject\":\"test\",\"body\":\"test\",\"channel\":\"address\",\"state\":\"received\",\"priority\":1,\"relatedContacts\":[{\"id\":null,\"name\":\"???\",\"surname\":\"???\",\"category\":\"unknown\",\"ssncode\":null,\"professionalInfo\":null}]}]") ?: false)

    }

    @Test
    fun `it should retrieve a list of message Dto with filters and pagination`() {
        val response : ResponseEntity<String> = restTemplate.exchange("/API/messages/?channel=address&pageNumber=1&limit=1", HttpMethod.GET, HttpEntity<String>(headers), String::class.java)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.hasBody())
        assert(response.body?.contains("\"messages\":[{\"id\":1001,\"sender\":\"Street Test, 12, Turin, Italy\",\"date\":\"2024-05-16\",\"subject\":\"test\",\"body\":\"test\",\"channel\":\"address\",\"state\":\"received\",\"priority\":1,\"relatedContacts\":[{\"id\":null,\"name\":\"???\",\"surname\":\"???\",\"category\":\"unknown\",\"ssncode\":null,\"professionalInfo\":null}]}]") ?: false)

    }

    @Test
    fun `it should raises an error trying to retrieve messages with invalid state`() {
        val response : ResponseEntity<String> = restTemplate.exchange("/API/messages/?state=invalid&pageNumber=1&limit=1", HttpMethod.GET, HttpEntity<String>(headers), String::class.java)
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `it should raises an error trying to retrieve messages with invalid priority`() {
        val response : ResponseEntity<String> = restTemplate.exchange("/API/messages/?priority=10&pageNumber=1&limit=1", HttpMethod.GET,HttpEntity<String>(headers), String::class.java)
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `it should retrieve the message Dto with the given an id`() {
        val response : ResponseEntity<String> = restTemplate.exchange("/API/messages/1001", HttpMethod.GET,HttpEntity<String>(headers), String::class.java)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.hasBody())
        assert(response.body?.contains("\"sender\":\"Street Test, 12, Turin, Italy\",\"date\":\"2024-05-16\",\"subject\":\"test\",\"body\":\"test\",\"channel\":\"address\",\"state\":\"received\",\"priority\":1,\"relatedContacts\":[{\"id\":null,\"name\":\"???\",\"surname\":\"???\",\"category\":\"unknown\",\"ssncode\":null,\"professionalInfo\":null}]") ?: false)

    }

    @Test
    fun `it should retrieve the message dto with the given id, returning Unknown Contact`() {
        val response : ResponseEntity<String> = restTemplate.exchange("/API/messages/10010", HttpMethod.GET, HttpEntity<String>(headers),String::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `it should correctly change message's state`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val stateDTO = StateWithMessageDTO("read", "test comment")
        val entity = HttpEntity(stateDTO, headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/messages/1001",
            HttpMethod.POST, entity , String::class.java)
        assert(response.statusCode == HttpStatus.OK)
    }

    @Test
    fun `it should raises an error not finding the message with the given id trying to update state`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val stateDTO = StateWithMessageDTO("read", "test comment")
        val entity = HttpEntity(stateDTO, headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/messages/10011",
            HttpMethod.POST, entity , String::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `it should correctly retrieve message's history`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val response : ResponseEntity<String> = restTemplate.exchange("/API/messages/1001/history", HttpMethod.GET, HttpEntity<String>(headers), String::class.java)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.hasBody())
        assert(response.body?.contains("\"state\":\"RECEIVED\",\"date\":\"2024-05-16T10:00:00\",\"comment\":\"test\"") ?: false)

    }

    @Test
    fun `it should raises an error not finding the message with the given id trying to retrieve history`() {
        val response : ResponseEntity<String> = restTemplate.exchange("/API/messages/10011/history", HttpMethod.GET, HttpEntity<String>(headers),String::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `it should correctly update message's priority`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity( "", headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/messages/1001/priority/?value=1",
            HttpMethod.PUT, entity , String::class.java)
        assert(response.statusCode == HttpStatus.OK)
    }

    @Test
    fun `it should raises an error not finding the message with the given id trying to update priority`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity( "", headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/messages/10011/priority/?value=1",
            HttpMethod.PUT, entity , String::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `it should raises an invalid priority exception trying to update priority`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity( "", headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/messages/1001/priority/?value=50",
            HttpMethod.PUT, entity , String::class.java)
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
    }

}