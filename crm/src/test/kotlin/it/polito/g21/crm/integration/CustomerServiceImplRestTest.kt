package it.polito.g21.crm.integration

import it.polito.g21.crm.models.GeneralContactDTO
import it.polito.g21.crm.models.NoteDTO
import jakarta.annotation.PostConstruct
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.io.Resource
import org.springframework.http.*
import org.springframework.jdbc.core.JdbcTemplate
import java.util.logging.Logger
import java.net.URI


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class CustomerServiceImplRestTest : IntegrationTest(){

    val accessToken = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI4Y3hkcHZoTGVicGdYcmhvRGl5ZjFNNmVDY01UeE42NEt1YnRGUnlIdlB3In0.eyJleHAiOjE3Mjg1NjI2NTYsImlhdCI6MTcxODE5NDY1NiwiYXV0aF90aW1lIjoxNzE4MTk0NjU2LCJqdGkiOiIwODg0Zjc2My1mNTg0LTQ2NjItYjhhMC0xZmI3MTc0YTU0OTEiLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjkwOTAvcmVhbG1zL2NybSIsImF1ZCI6ImFjY291bnQiLCJzdWIiOiI1NmRhMWFkOS1iOGVmLTRhYTItYTQ5My1hNmU0MWVjYzBjYTYiLCJ0eXAiOiJCZWFyZXIiLCJhenAiOiJjcm1jbGllbnQiLCJub25jZSI6Ik9YMGlMc3RWMHRvZEFUYzhWMzl1a0ItNmxKdnVETzdfbmxORzhXTThvR2siLCJzZXNzaW9uX3N0YXRlIjoiMzRhNTYwYjctZWIxNy00MTY4LWEzZGItNTIwYTg2NDYwYjAzIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vbG9jYWxob3N0OjgwODAiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm1hbmFnZXIiLCJvZmZsaW5lX2FjY2VzcyIsImRlZmF1bHQtcm9sZXMtY3JtIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6Im9wZW5pZCBlbWFpbCBwcm9maWxlIG9mZmxpbmVfYWNjZXNzIiwic2lkIjoiMzRhNTYwYjctZWIxNy00MTY4LWEzZGItNTIwYTg2NDYwYjAzIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJuYW1lIjoiRWRvYXJkbyBNb3JlbGxvIiwicHJlZmVycmVkX3VzZXJuYW1lIjoiZWRvbW9yZWxsbyIsImdpdmVuX25hbWUiOiJFZG9hcmRvIiwiZmFtaWx5X25hbWUiOiJNb3JlbGxvIiwiZW1haWwiOiJlZG9hcmRvLm1vcmVsbG8uMTJAZ21haWwuY29tIn0.g3SU2kw421Gj5zFhjrPKKL-opeCTSuXHb_fXHa6v_Z_SGNd-FBjUz5FF7shT2UrSVzJFD5B2XBV1x9vdfZfqM-iu_YrHtEuWpFvRr4FdEb_dgZfSERZfidhB-jSSoIa66fUcBgppfCVAIrPWYxirRog6GUCjHdZujAOGA7nW-eqai7JomoGlOmwxSE11zXbcJFQcuWu1jMtkAaVTfFSE2VjBq6dfJV2Uuz07qUvYts-oKvv_Abq2oD78D92Z6UZwbkdC5M0N009dnuYZGW1OcA1dSKkvJzUDOo9_bVC4qzmxBbMy8RxYJ8zTd1OjdVhh1u5BLEHhiwM4Z9tii6-GxA"

    val headers = HttpHeaders().apply {
        set("Authorization", "Bearer $accessToken")
    }

    val logger: Logger = Logger.getLogger("CustomerServiceImplRestTest")

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
    fun `it should retrieve all customers`(){
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/customers/?category=customer",
            HttpMethod.GET,
            HttpEntity<String>(headers),
            String::class.java
        )

        //val response: ResponseEntity<String> = restTemplate.getForEntity("/API/customers/?category=customer", String::class.java)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.hasBody())
        assert(response.body?.contains("\"id\":1001,\"name\":\"Mario\",\"surname\":\"Rossi\",\"category\":\"customer\",\"ssncode\":\"123456789\",\"mailList\":[{\"id\":1001,\"mail\":\"mariorossi@gmail.com\"}],\"addressList\":[],\"telephoneList\":[],\"professionalInfo\":null") ?: false)
        assert(response.body?.contains("\"id\":1002,\"name\":\"Giacomo\",\"surname\":\"Verdi\",\"category\":\"customer\",\"ssncode\":\"123456788\",\"mailList\":[],\"addressList\":[],\"telephoneList\":[],\"professionalInfo\":null") ?: false)
    }

    @Test
    fun `it should retrieve all customer with filters`(){
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/customers/?name=Mario&surname=Rossi",
            HttpMethod.GET,
            HttpEntity<String>(headers),
            String::class.java
        )
        //val response: ResponseEntity<String> = restTemplate.getForEntity("/API/customers/?name=Mario&surname=Rossi", String::class.java)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.hasBody())
        assert(response.body?.contains("\"id\":1001,\"name\":\"Mario\",\"surname\":\"Rossi\",\"category\":\"customer\",\"ssncode\":\"123456789\",\"mailList\":[{\"id\":1001,\"mail\":\"mariorossi@gmail.com\"}],\"addressList\":[],\"telephoneList\":[],\"professionalInfo\":null") ?: false)
    }

    @Test
    fun `it should retrieve no customer with this filter`(){
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/customers/?name=Giovanni&surname=Rossi",
            HttpMethod.GET,
            HttpEntity<String>(headers),
            String::class.java
        )

        //val response: ResponseEntity<String> = restTemplate.getForEntity("/API/customers/?name=Giovanni&surname=Rossi", String::class.java)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.hasBody())
        assert(response.body?.contains("\"customers\":[]") ?: false)
    }

    @Test
    fun `it should create a customer entity`() {
        val customer = GeneralContactDTO(null, "Edoardo", "Bianchetto", "customer", "MRLDR42353F", null)

        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(customer, headers)
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/customers/",
            HttpMethod.POST,
            entity,
            String::class.java
        )

        //val response : ResponseEntity<String> = restTemplate.postForEntity("/API/customers/", customer, String::class.java)
        assert(response.statusCode == HttpStatus.CREATED)
    }

    @Test
    fun `it should raise an error trying to create a customer but using a contact with category=professional`() {

        val customer = GeneralContactDTO(null, "Edoardo", "Bianchetto", "professional", "MRLDR42353F", null)
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(customer, headers)
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/customers/",
            HttpMethod.POST,
            entity,
            String::class.java
        )
        //val response : ResponseEntity<String> = restTemplate.postForEntity("/API/customers/",customer, String::class.java)
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `it should raise an error trying to create a customer but using a contact with a non-existing category`() {
        val customer = GeneralContactDTO(null, "Edoardo", "Bianchetto", "non-existing", "MRLDR42353F", null)

        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(customer, headers)
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/customers/",
            HttpMethod.POST,
            entity,
            String::class.java
        )

        //val response : ResponseEntity<String> = restTemplate.postForEntity("/API/customers/",customer, String::class.java)
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `it should retrieve a customer specifying the id`(){
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/customers/1001",
            HttpMethod.GET,
            HttpEntity<String>(headers),
            String::class.java
        )

        //val response: ResponseEntity<String> = restTemplate.getForEntity("/API/customers/1001", String::class.java)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.hasBody())
        assert(response.body?.contains("\"id\":1001,\"name\":\"Mario\",\"surname\":\"Rossi\",\"category\":\"customer\",\"ssncode\":\"123456789\",\"mailList\":[{\"id\":1001,\"mail\":\"mariorossi@gmail.com\"}],\"addressList\":[],\"telephoneList\":[],\"professionalInfo\":null") ?: false)
    }

    @Test
    fun `it should raise an error not finding the customer`() {
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/customers/1000",
            HttpMethod.GET,
            HttpEntity<String>(headers),
            String::class.java
        )

        //val response: ResponseEntity<String> = restTemplate.getForEntity("/API/customers/1000", String::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `it should raise an error in finding the contact who is not a customer`() {
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/customers/1003",
            HttpMethod.GET,
            HttpEntity<String>(headers),
            String::class.java
        )

        //val response: ResponseEntity<String> = restTemplate.getForEntity("/API/customers/1003", String::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `it should delete a customer`() {
        val requestEntity = RequestEntity<Any>(headers, HttpMethod.DELETE, URI("/API/customers/1002"))
        val response: ResponseEntity<String> = restTemplate.exchange(requestEntity, String::class.java)
        assert(response.statusCode == HttpStatus.NO_CONTENT)
    }

    @Test
    fun `it should raise an error in deleting a customer that does not exist`() {
        val requestEntity = RequestEntity<Any>(headers, HttpMethod.DELETE, URI("/API/customers/1000"))
        val response: ResponseEntity<String> = restTemplate.exchange(requestEntity, String::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
    }


    @Test
    fun `it should update a customer`() {
        val customer = GeneralContactDTO(null, "Edoardo", surname = "Bianchetto", category =  "customer", "123456789", professionalInfo = null)

        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(customer, headers)
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/customers/1002",
            HttpMethod.PUT,
            entity,
            String::class.java
        )

        //val response : ResponseEntity<String> = restTemplate.exchange("/API/customers/1002", HttpMethod.PUT, HttpEntity(customer) , String::class.java)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.hasBody())
        assert(response.body?.contains("\"id\":1002,\"name\":\"Edoardo\",\"surname\":\"Bianchetto\",\"category\":\"customer\",\"ssncode\":\"123456789\",\"mailList\":[],\"addressList\":[],\"telephoneList\":[],\"professionalInfo\":null") ?: false)
    }

    @Test
    fun `it should raise an error in updating a customer that does not exist`() {
        val customer = GeneralContactDTO(null, "Edoardo", surname = "Bianchetto", category =  "customer", "123456789", professionalInfo = null)
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(customer, headers)
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/customers/1000",
            HttpMethod.PUT,
            entity,
            String::class.java
        )

        //val response : ResponseEntity<String> = restTemplate.exchange("/API/customers/1000", HttpMethod.PUT, HttpEntity(customer) , String::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `it should raise an error in updating a customer changing category`() {
        val customer = GeneralContactDTO(null, "Edoardo", surname = "Bianchetto", category =  "professional", "123456789", professionalInfo = null)
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(customer, headers)
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/customers/1001",
            HttpMethod.PUT,
            entity,
            String::class.java
        )

        //val response : ResponseEntity<String> = restTemplate.exchange("/API/customers/1001", HttpMethod.PUT, HttpEntity(customer) , String::class.java)
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
    }

}