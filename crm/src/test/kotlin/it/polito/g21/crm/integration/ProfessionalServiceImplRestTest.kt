package it.polito.g21.crm.integration

import it.polito.g21.crm.models.GeneralContactDTO
import it.polito.g21.crm.models.ProfessionalDTO
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
import java.net.URI

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class ProfessionalServiceImplRestTest: IntegrationTest() {

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
    fun `it should get all professionals`(){
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/professionals/?category=professional",
            HttpMethod.GET,
            HttpEntity<String>(headers),
            String::class.java
        )

        //val response: ResponseEntity<String> = restTemplate.getForEntity("/API/professionals/?category=professional", String::class.java)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.hasBody())
        assert(response.body?.contains("\"professionals\":[{\"id\":5003,\"name\":\"Professional\",\"surname\":\"Test\",\"category\":\"professional\",\"ssncode\":\"123456787\",\"mailList\":[{\"id\":1002,\"mail\":\"test@mail.com\"}],\"addressList\":[{\"id\":1002,\"address\":\"Street Test, 11, Turin, Italy\"}],\"telephoneList\":[{\"id\":1001,\"number\":\"3568899777\"}],\"professionalInfo\":{\"skills\":\"frontend\",\"employmentState\":\"employed\",\"location\":\"Italy\",\"dailyRate\":4.2,\"linkedJobs\":[]}},{\"id\":1003,\"name\":\"Mario\",\"surname\":\"Giallo\",\"category\":\"professional\",\"ssncode\":\"123456787\",\"mailList\":[],\"addressList\":[],\"telephoneList\":[],\"professionalInfo\":{\"skills\":\"backend\",\"employmentState\":\"available\",\"location\":\"Italy\",\"dailyRate\":2.4,\"linkedJobs\":[{\"id\":1211,\"description\":\"aborted job offer\",\"requiredSkills\":\"frontend\",\"duration\":12,\"status\":\"aborted\",\"profitMargin\":2.9,\"customer\":{\"id\":1001,\"name\":\"Mario\",\"surname\":\"Rossi\",\"category\":\"customer\",\"ssncode\":\"123456789\",\"mailList\":[{\"id\":1001,\"mail\":\"mariorossi@gmail.com\"}],\"addressList\":[],\"telephoneList\":[],\"professionalInfo\":null},\"professionalId\":1003}]}},{\"id\":1203,\"name\":\"Andrea\",\"surname\":\"Verde\",\"category\":\"professional\",\"ssncode\":\"123456787\",\"mailList\":[],\"addressList\":[],\"telephoneList\":[],\"professionalInfo\":{\"skills\":\"frontend\",\"employmentState\":\"available\",\"location\":\"Italy\",\"dailyRate\":3.2,\"linkedJobs\":[]}},{\"id\":1204,\"name\":\"Andrea\",\"surname\":\"Bianchino\",\"category\":\"professional\",\"ssncode\":\"123456787\",\"mailList\":[],\"addressList\":[],\"telephoneList\":[],\"professionalInfo\":{\"skills\":\"frontend\",\"employmentState\":\"available\",\"location\":\"Italy\",\"dailyRate\":3.0,\"linkedJobs\":[]}},{\"id\":1304,\"name\":\"Giuseppe\",\"surname\":\"Viola\",\"category\":\"professional\",\"ssncode\":\"123456787\",\"mailList\":[],\"addressList\":[],\"telephoneList\":[],\"professionalInfo\":{\"skills\":\"frontend\",\"employmentState\":\"available\",\"location\":\"Italy\",\"dailyRate\":3.0,\"linkedJobs\":[]}},{\"id\":1404,\"name\":\"Giuseppe\",\"surname\":\"Sasso\",\"category\":\"professional\",\"ssncode\":\"123456787\",\"mailList\":[],\"addressList\":[],\"telephoneList\":[],\"professionalInfo\":{\"skills\":\"frontend\",\"employmentState\":\"employed\",\"location\":\"Italy\",\"dailyRate\":4.2,\"linkedJobs\":[{\"id\":1122,\"description\":\"good job offer\",\"requiredSkills\":\"backend\",\"duration\":13,\"status\":\"consolidated\",\"profitMargin\":2.0,\"customer\":{\"id\":1001,\"name\":\"Mario\",\"surname\":\"Rossi\",\"category\":\"customer\",\"ssncode\":\"123456789\",\"mailList\":[{\"id\":1001,\"mail\":\"mariorossi@gmail.com\"}],\"addressList\":[],\"telephoneList\":[],\"professionalInfo\":null},\"professionalId\":1404},{\"id\":1123,\"description\":\"that is a good job\",\"requiredSkills\":\"backend\",\"duration\":11,\"status\":\"done\",\"profitMargin\":2.9,\"customer\":{\"id\":1001,\"name\":\"Mario\",\"surname\":\"Rossi\",\"category\":\"customer\",\"ssncode\":\"123456789\",\"mailList\":[{\"id\":1001,\"mail\":\"mariorossi@gmail.com\"}],\"addressList\":[],\"telephoneList\":[],\"professionalInfo\":null},\"professionalId\":1404}]}}]") ?: false)
    }

    @Test
    fun `it should retrieve all professionals with filters`(){
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/professionals/?name=Mario&surname=Giallo",
            HttpMethod.GET,
            HttpEntity<String>(headers),
            String::class.java
        )

        //val response: ResponseEntity<String> = restTemplate.getForEntity("/API/professionals/?name=Mario&surname=Giallo", String::class.java)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.hasBody())
        assert(response.body?.contains("\"professionals\":[{\"id\":1003,\"name\":\"Mario\",\"surname\":\"Giallo\",\"category\":\"professional\",\"ssncode\":\"123456787\",\"mailList\":[],\"addressList\":[],\"telephoneList\":[],\"professionalInfo\":{\"skills\":\"backend\",\"employmentState\":\"available\",\"location\":\"Italy\",\"dailyRate\":2.4,\"linkedJobs\":[{\"id\":1211,\"description\":\"aborted job offer\",\"requiredSkills\":\"frontend\",\"duration\":12,\"status\":\"aborted\",\"profitMargin\":2.9,\"customer\":{\"id\":1001,\"name\":\"Mario\",\"surname\":\"Rossi\",\"category\":\"customer\",\"ssncode\":\"123456789\",\"mailList\":[{\"id\":1001,\"mail\":\"mariorossi@gmail.com\"}],\"addressList\":[],\"telephoneList\":[],\"professionalInfo\":null},\"professionalId\":1003}]}}]") ?: false)
    }

    @Test
    fun `it should retrieve no professional with this filter`(){
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/professionals/?name=Giovanni&surname=Rossi",
            HttpMethod.GET,
            HttpEntity<String>(headers),
            String::class.java
        )

        //val response: ResponseEntity<String> = restTemplate.getForEntity("/API/professionals/?name=Giovanni&surname=Rossi", String::class.java)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.hasBody())
        assert(response.body?.contains("\"professionals\":[]") ?: false)
    }


    @Test
    fun `it should create a professional`(){
        val professional = GeneralContactDTO(null, "Edoardo", "Bianchetto", "professional", "MRLDR42353F", ProfessionalDTO("frontend","employed","Italy", 5.6, null))
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(professional, headers)
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/professionals/",
            HttpMethod.POST,
            entity,
            String::class.java
        )

        //val response : ResponseEntity<Unit> = restTemplate.postForEntity("/API/professionals/", professional, Unit::class.java)
        assert(response.statusCode == HttpStatus.CREATED)
    }


    @Test
    fun `it should raise an error finding 'customer' category rather than 'professional'`() {
        val professional = GeneralContactDTO(null, "Edoardo", "Bianchetto", "customer", "MRLDR42353F",  ProfessionalDTO("frontend","employed","Italy", 5.6, null))
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(professional, headers)
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/professionals/",
            HttpMethod.POST,
            entity,
            String::class.java
        )

        //val response : ResponseEntity<Unit> = restTemplate.postForEntity("/API/professionals/", professional, Unit::class.java)
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
    }


    @Test
    fun `it should raise an error not having professional info in creation`() {
        val professional = GeneralContactDTO(null, "Edoardo", "Bianchetto", "professional", "MRLDR42353F",  null)
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(professional, headers)
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/professionals/",
            HttpMethod.POST,
            entity,
            String::class.java
        )

        //val response : ResponseEntity<Unit> = restTemplate.postForEntity("/API/professionals/", professional, Unit::class.java)
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `it should raise having invalid category in creation`() {
        val professional = GeneralContactDTO(null, "Edoardo", "Bianchetto", "error", "MRLDR42353F",   ProfessionalDTO("frontend","employed","Italy", 5.6, null))
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(professional, headers)
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/professionals/",
            HttpMethod.POST,
            entity,
            String::class.java
        )

        //val response : ResponseEntity<Unit> = restTemplate.postForEntity("/API/professionals/", professional, Unit::class.java)
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
    }


    @Test
    fun `it should raise an error having invalid emp state in creation`() {
        val professional = GeneralContactDTO(null, "Edoardo", "Bianchetto", "professional", "MRLDR42353F",   ProfessionalDTO("frontend","invalid","Italy", 5.6, null))
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(professional, headers)
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/professionals/",
            HttpMethod.POST,
            entity,
            String::class.java
        )

        //val response : ResponseEntity<Unit> = restTemplate.postForEntity("/API/professionals/", professional, Unit::class.java)
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `it should correctly retrieve the Professional specifying id`() {
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/professionals/1003",
            HttpMethod.GET,
            HttpEntity<String>(headers),
            String::class.java
        )

        //val response: ResponseEntity<String> = restTemplate.getForEntity("/API/professionals/1003", String::class.java)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.hasBody())
        assert(response.body?.contains("\"id\":1003,\"name\":\"Mario\",\"surname\":\"Giallo\",\"category\":\"professional\",\"ssncode\":\"123456787\",\"mailList\":[],\"addressList\":[],\"telephoneList\":[],\"professionalInfo\":{\"skills\":\"backend\",\"employmentState\":\"available\",\"location\":\"Italy\",\"dailyRate\":2.4,\"linkedJobs\":[{\"id\":1211,\"description\":\"aborted job offer\",\"requiredSkills\":\"frontend\",\"duration\":12,\"status\":\"aborted\",\"profitMargin\":2.9,\"customer\":{\"id\":1001,\"name\":\"Mario\",\"surname\":\"Rossi\",\"category\":\"customer\",\"ssncode\":\"123456789\",\"mailList\":[{\"id\":1001,\"mail\":\"mariorossi@gmail.com\"}],\"addressList\":[],\"telephoneList\":[],\"professionalInfo\":null},\"professionalId\":1003}]}") ?: false)
    }

    @Test
    fun `it should raise an error finding a Contact that it is not a Professional`() {
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/professionals/1002",
            HttpMethod.GET,
            HttpEntity<String>(headers),
            String::class.java
        )

        //val response: ResponseEntity<String> = restTemplate.getForEntity("/API/professionals/1002", String::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `it should raise an error not finding the Professional`(){
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/professionals/1005",
            HttpMethod.GET,
            HttpEntity<String>(headers),
            String::class.java
        )

        //val response: ResponseEntity<String> = restTemplate.getForEntity("/API/professionals/1005", String::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `it should correctly delete the Professional`() {
        val requestEntity = RequestEntity<Any>(headers, HttpMethod.DELETE, URI("/API/professionals/5003"))
        val response: ResponseEntity<String> = restTemplate.exchange(requestEntity, String::class.java)
        assert(response.statusCode == HttpStatus.NO_CONTENT)
    }

    @Test
    fun `it should raise an error on deleting a Professional that not exists`() {
        val requestEntity = RequestEntity<Any>(headers, HttpMethod.DELETE, URI("/API/professionals/1000"))
        val response: ResponseEntity<String> = restTemplate.exchange(requestEntity, String::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `it should update a professional`() {
        val professionalInfo = ProfessionalDTO("frontend","employed","Italy", 5.6,null)
        val professional = GeneralContactDTO(null, "Edoardo", surname = "Bianchetto", category =  "professional", "123456789", professionalInfo = professionalInfo)
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(professional, headers)
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/professionals/5003",
            HttpMethod.PUT,
            entity,
            String::class.java
        )

        //val response : ResponseEntity<String> = restTemplate.exchange("/API/professionals/5003", HttpMethod.PUT, HttpEntity(professional) , String::class.java)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.hasBody())
        assert(response.body?.contains("\"id\":5003,\"name\":\"Edoardo\",\"surname\":\"Bianchetto\",\"category\":\"professional\",\"ssncode\":\"123456789\",\"mailList\":[{\"id\":1002,\"mail\":\"test@mail.com\"}],\"addressList\":[{\"id\":1002,\"address\":\"Street Test, 11, Turin, Italy\"}],\"telephoneList\":[{\"id\":1001,\"number\":\"3568899777\"}],\"professionalInfo\":{\"skills\":\"frontend\",\"employmentState\":\"employed\",\"location\":\"Italy\",\"dailyRate\":5.6,\"linkedJobs\":[]}") ?: false)
    }

    @Test
    fun `it should raise an error in updating a professional that does not exist`() {
        val professionalInfo = ProfessionalDTO("frontend","employed","Italy", 5.6,null)
        val professional = GeneralContactDTO(null, "Edoardo", surname = "Bianchetto", category =  "professional", "123456789", professionalInfo = professionalInfo)
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(professional, headers)
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/professionals/1020",
            HttpMethod.PUT,
            entity,
            String::class.java
        )

        //val response : ResponseEntity<String> = restTemplate.exchange("/API/professionals/1020", HttpMethod.PUT, HttpEntity(professional) , String::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `it should raise an error in updating a professional changing category`() {
        val professionalInfo = ProfessionalDTO("frontend","employed","Italy", 5.6,null)
        val professional = GeneralContactDTO(null, "Edoardo", surname = "Bianchetto", category =  "customer", "123456789", professionalInfo = professionalInfo)
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(professional, headers)
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/professionals/1003",
            HttpMethod.PUT,
            entity,
            String::class.java
        )

        //val response : ResponseEntity<String> = restTemplate.exchange("/API/professionals/1003", HttpMethod.PUT, HttpEntity(professional) , String::class.java)
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
    }

}