package it.polito.g21.crm.integration

import it.polito.g21.crm.models.JobOfferDTO
import it.polito.g21.crm.models.JobStatusDTO
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class JobServiceImplRestTest : IntegrationTest() {

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
    fun `it should create a job offer`() {
        val jobToAdd = JobOfferDTO(null, "testDesc", "testSkills", 1, null, 1.2, null, null)
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(jobToAdd, headers)
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/joboffers/?customer=1002",
            HttpMethod.POST,
            entity,
            String::class.java
        )


        //val response : ResponseEntity<String> = restTemplate.postForEntity("/API/joboffers/?customer=1002",jobToAdd, String::class.java)
        assert(response.statusCode == HttpStatus.CREATED)
    }


    @Test
    fun `it should raise an error not finding the customer trying to create a job offer`() {
        val jobToAdd = JobOfferDTO(null, "testDesc", "testSkills", 1, null, 1.2, null, null)
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(jobToAdd, headers)
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/joboffers/?customer=1003",
            HttpMethod.POST,
            entity,
            String::class.java
        )

        //val response : ResponseEntity<String> = restTemplate.postForEntity("/API/joboffers/?customer=1003",jobToAdd, String::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
    }


    @Test
    fun `it should retrieve a job offer having its id`() {
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/joboffers/1111",
            HttpMethod.GET,
            HttpEntity<String>(headers),
            String::class.java
        )

        //val response: ResponseEntity<String> = restTemplate.getForEntity("/API/joboffers/1111", String::class.java)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.hasBody())
        assert(response.body?.contains("\"id\":1111,\"description\":\"that is a good job\",\"requiredSkills\":\"frontend\",\"duration\":12,\"status\":\"created\",\"profitMargin\":2.9,\"customer\":{\"id\":1001,\"name\":\"Mario\",\"surname\":\"Rossi\",\"category\":\"customer\",\"ssncode\":\"123456789\",\"mailList\":[{\"id\":1001,\"mail\":\"mariorossi@gmail.com\"}],\"addressList\":[],\"telephoneList\":[],\"professionalInfo\":null},\"professionalId\":null") ?: false)
    }


    @Test
    fun `it should raise an error not finding the job offer trying to retrieve it`() {
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/joboffers/1245",
            HttpMethod.GET,
            HttpEntity<String>(headers),
            String::class.java
        )

        //val response: ResponseEntity<String> = restTemplate.getForEntity("/API/joboffers/1245", String::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `it should retrieve all open job Offers filtering customers`() {
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/joboffers/?status=open&contactId=1001&category=customer",
            HttpMethod.GET,
            HttpEntity<String>(headers),
            String::class.java
        )

        //val response: ResponseEntity<String> = restTemplate.getForEntity("/API/joboffers/?status=open&contactId=1001&category=customer", String::class.java)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.hasBody())
        assert(response.body?.contains("\"jobOffers\":[{\"id\":1222,\"description\":\"good job offer\",\"requiredSkills\":\"backend\",\"duration\":13,\"status\":\"candidate_proposal\",\"profitMargin\":2.8,\"customer\":{\"id\":1001,\"name\":\"Mario\",\"surname\":\"Rossi\",\"category\":\"customer\",\"ssncode\":\"123456789\",\"mailList\":[{\"id\":1001,\"mail\":\"mariorossi@gmail.com\"}],\"addressList\":[],\"telephoneList\":[],\"professionalInfo\":null},\"professionalId\":null},{\"id\":1111,\"description\":\"that is a good job\",\"requiredSkills\":\"frontend\",\"duration\":12,\"status\":\"created\",\"profitMargin\":2.9,\"customer\":{\"id\":1001,\"name\":\"Mario\",\"surname\":\"Rossi\",\"category\":\"customer\",\"ssncode\":\"123456789\",\"mailList\":[{\"id\":1001,\"mail\":\"mariorossi@gmail.com\"}],\"addressList\":[],\"telephoneList\":[],\"professionalInfo\":null},\"professionalId\":null}]}") ?: false)
    }

    @Test
    fun `it should retrieve all open job Offers filtering customers with pagination`() {
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/joboffers/?status=open&contactId=1001&category=customer&pageNumber=1&limit=1",
            HttpMethod.GET,
            HttpEntity<String>(headers),
            String::class.java
        )

        //val response: ResponseEntity<String> = restTemplate.getForEntity("/API/joboffers/?status=open&contactId=1001&category=customer&pageNumber=1&limit=1", String::class.java)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.hasBody())

        assert(response.body?.contains("\"jobOffers\":[{\"id\":1222,\"description\":\"good job offer\",\"requiredSkills\":\"backend\",\"duration\":13,\"status\":\"candidate_proposal\",\"profitMargin\":2.8,\"customer\":{\"id\":1001,\"name\":\"Mario\",\"surname\":\"Rossi\",\"category\":\"customer\",\"ssncode\":\"123456789\",\"mailList\":[{\"id\":1001,\"mail\":\"mariorossi@gmail.com\"}],\"addressList\":[],\"telephoneList\":[],\"professionalInfo\":null},\"professionalId\":null}]}") ?: false)
    }

    @Test
    fun `it should retrieve all accepted job Offers filtering professional`() {
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/joboffers/?status=accepted&contactId=1404&category=professional",
            HttpMethod.GET,
            HttpEntity<String>(headers),
            String::class.java
        )

        //val response: ResponseEntity<String> = restTemplate.getForEntity("/API/joboffers/?status=accepted&contactId=1404&category=professional", String::class.java)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.hasBody())
        assert(response.body?.contains("\"jobOffers\":[{\"id\":1123,\"description\":\"that is a good job\",\"requiredSkills\":\"backend\",\"duration\":11,\"status\":\"done\",\"profitMargin\":2.9,\"customer\":{\"id\":1001,\"name\":\"Mario\",\"surname\":\"Rossi\",\"category\":\"customer\",\"ssncode\":\"123456789\",\"mailList\":[{\"id\":1001,\"mail\":\"mariorossi@gmail.com\"}],\"addressList\":[],\"telephoneList\":[],\"professionalInfo\":null},\"professionalId\":1404}") ?: false)
    }

    @Test
    fun `it should retrieve all aborted job Offers filtering professional`() {
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/joboffers/?status=aborted&contactId=1003&category=professional",
            HttpMethod.GET,
            HttpEntity<String>(headers),
            String::class.java
        )

        //val response: ResponseEntity<String> = restTemplate.getForEntity("/API/joboffers/?status=aborted&contactId=1003&category=professional", String::class.java)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.hasBody())
        assert(response.body?.contains("\"jobOffers\":[{\"id\":1211,\"description\":\"aborted job offer\",\"requiredSkills\":\"frontend\",\"duration\":12,\"status\":\"aborted\",\"profitMargin\":2.9,\"customer\":{\"id\":1001,\"name\":\"Mario\",\"surname\":\"Rossi\",\"category\":\"customer\",\"ssncode\":\"123456789\",\"mailList\":[{\"id\":1001,\"mail\":\"mariorossi@gmail.com\"}],\"addressList\":[],\"telephoneList\":[],\"professionalInfo\":null},\"professionalId\":1003}]") ?: false)
    }

    @Test
    fun `it should raise an error when category is neither 'customer' nor 'professional' trying to retrieve job offers`() {
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/joboffers/?status=aborted&contactId=1003&category=invalid",
            HttpMethod.GET,
            HttpEntity<String>(headers),
            String::class.java
        )

        //val response: ResponseEntity<String> = restTemplate.getForEntity("/API/joboffers/?status=aborted&contactId=1003&category=invalid", String::class.java)
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `it should raise an error when 'contactId' or 'category' are not both present or absent trying to retrieve job offers`() {
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/joboffers/?status=open&category=professional",
            HttpMethod.GET,
            HttpEntity<String>(headers),
            String::class.java
        )

        //val response: ResponseEntity<String> = restTemplate.getForEntity("/API/joboffers/?status=open&category=professional", String::class.java)
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `it should raise an error when status is invalid trying to retrieve job offers`() {
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/joboffers/?status=invalid&contactId=1003&category=professional",
            HttpMethod.GET,
            HttpEntity<String>(headers),
            String::class.java
        )

        //val response: ResponseEntity<String> = restTemplate.getForEntity("/API/joboffers/?status=invalid&contactId=1003&category=professional", String::class.java)
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `it should raise an error trying to filter for professionalId on open job offers`() {
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/joboffers/?status=open&contactId=1003&category=professional",
            HttpMethod.GET,
            HttpEntity<String>(headers),
            String::class.java
        )

        //val response: ResponseEntity<String> = restTemplate.getForEntity("/API/joboffers/?status=open&contactId=1003&category=professional", String::class.java)
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `it should change job offer status`() {
        val jobOffer = JobStatusDTO("selection_phase", "testNote", null)

        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(jobOffer, headers)
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/joboffers/1111",
            HttpMethod.POST,
            entity,
            String::class.java
        )

        //val response : ResponseEntity<String> = restTemplate.exchange("/API/joboffers/1111", HttpMethod.POST, HttpEntity(jobOffer) , String::class.java)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.hasBody())
        assert(response.body?.contains("\"id\":1111,\"description\":\"that is a good job\",\"requiredSkills\":\"frontend\",\"duration\":12,\"status\":\"selection_phase\",\"profitMargin\":2.9,\"customer\":{\"id\":1001,\"name\":\"Mario\",\"surname\":\"Rossi\",\"category\":\"customer\",\"ssncode\":\"123456789\",\"mailList\":[{\"id\":1001,\"mail\":\"mariorossi@gmail.com\"}],\"addressList\":[],\"telephoneList\":[],\"professionalInfo\":null},\"professionalId\":null") ?: false)

    }

    @Test
    fun `it should change job offer status and attach a professional`() {
        val jobOffer = JobStatusDTO("consolidated", "testNote", 1204)
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(jobOffer, headers)
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/joboffers/1222",
            HttpMethod.POST,
            entity,
            String::class.java
        )

        //val response : ResponseEntity<String> = restTemplate.exchange("/API/joboffers/1222", HttpMethod.POST, HttpEntity(jobOffer) , String::class.java)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.hasBody())
        assert(response.body?.contains("\"id\":1222,\"description\":\"good job offer\",\"requiredSkills\":\"backend\",\"duration\":13,\"status\":\"consolidated\",\"profitMargin\":2.8,\"customer\":{\"id\":1001,\"name\":\"Mario\",\"surname\":\"Rossi\",\"category\":\"customer\",\"ssncode\":\"123456789\",\"mailList\":[{\"id\":1001,\"mail\":\"mariorossi@gmail.com\"}],\"addressList\":[],\"telephoneList\":[],\"professionalInfo\":null},\"professionalId\":1204") ?: false)
    }

    @Test
    fun `it should change job offer status to done`() {
        val jobOffer = JobStatusDTO("done", "testNote", 1404)
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(jobOffer, headers)
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/joboffers/1122",
            HttpMethod.POST,
            entity,
            String::class.java
        )

        //val response : ResponseEntity<String> = restTemplate.exchange("/API/joboffers/1122", HttpMethod.POST, HttpEntity(jobOffer) , String::class.java)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.hasBody())
        assert(response.body?.contains("\"id\":1122,\"description\":\"good job offer\",\"requiredSkills\":\"backend\",\"duration\":13,\"status\":\"done\",\"profitMargin\":2.0,\"customer\":{\"id\":1001,\"name\":\"Mario\",\"surname\":\"Rossi\",\"category\":\"customer\",\"ssncode\":\"123456789\",\"mailList\":[{\"id\":1001,\"mail\":\"mariorossi@gmail.com\"}],\"addressList\":[],\"telephoneList\":[],\"professionalInfo\":null},\"professionalId\":1404") ?: false)
    }

    @Test
    fun `it should change job offer status and delete professional's link`() {
        val jobOffer = JobStatusDTO("selection_phase", "testNote", null)
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(jobOffer, headers)
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/joboffers/1122",
            HttpMethod.POST,
            entity,
            String::class.java
        )

        //val response : ResponseEntity<String> = restTemplate.exchange("/API/joboffers/1122", HttpMethod.POST, HttpEntity(jobOffer) , String::class.java)
        assert(response.hasBody())
        assert(response.body?.contains("\"id\":1122,\"description\":\"good job offer\",\"requiredSkills\":\"backend\",\"duration\":13,\"status\":\"selection_phase\",\"profitMargin\":2.0,\"customer\":{\"id\":1001,\"name\":\"Mario\",\"surname\":\"Rossi\",\"category\":\"customer\",\"ssncode\":\"123456789\",\"mailList\":[{\"id\":1001,\"mail\":\"mariorossi@gmail.com\"}],\"addressList\":[],\"telephoneList\":[],\"professionalInfo\":null},\"professionalId\":null") ?: false)
        assert(response.statusCode == HttpStatus.OK)
    }

    @Test
    fun `it should raise an error not finding the job_offer trying to update it`() {
        val jobOffer = JobStatusDTO("selection_phase", "testNote", null)
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(jobOffer, headers)
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/joboffers/12333",
            HttpMethod.POST,
            entity,
            String::class.java
        )

        //val response : ResponseEntity<String> = restTemplate.exchange("/API/joboffers/12333", HttpMethod.POST, HttpEntity(jobOffer) , String::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)

    }

    @Test
    fun `it should raise an error having an invalid job offer state trying to change it`() {
        val jobOffer = JobStatusDTO("error_state", "testNote", null)
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(jobOffer, headers)
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/joboffers/1122",
            HttpMethod.POST,
            entity,
            String::class.java
        )

        //val response : ResponseEntity<String> = restTemplate.exchange("/API/joboffers/1122", HttpMethod.POST, HttpEntity(jobOffer) , String::class.java)
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
    }


    @Test
    fun `it should raise an error trying to update job offer's state into an invalid one`() {
        val jobOffer = JobStatusDTO("candidate_proposal", "testNote", null)
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(jobOffer, headers)
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/joboffers/1122",
            HttpMethod.POST,
            entity,
            String::class.java
        )

        //val response : ResponseEntity<String> = restTemplate.exchange("/API/joboffers/1122", HttpMethod.POST, HttpEntity(jobOffer) , String::class.java)
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `it should raise an error trying to update job offer into consolidated state without any professional provided`() {
        val jobOffer = JobStatusDTO("consolidated", "testNote", null)
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(jobOffer, headers)
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/joboffers/1222",
            HttpMethod.POST,
            entity,
            String::class.java
        )

        //val response : ResponseEntity<String> = restTemplate.exchange("/API/joboffers/1222", HttpMethod.POST, HttpEntity(jobOffer) , String::class.java)
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `it should raise an error trying to update job offer into consolidated state not finding the professional provided`(){
        val jobOffer = JobStatusDTO("consolidated", "testNote", 1000)
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(jobOffer, headers)
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/joboffers/1222",
            HttpMethod.POST,
            entity,
            String::class.java
        )

        //val response : ResponseEntity<String> = restTemplate.exchange("/API/joboffers/1222", HttpMethod.POST, HttpEntity(jobOffer) , String::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `it should compute the job offer's value`(){
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/joboffers/1122/value",
            HttpMethod.GET,
            HttpEntity<String>(headers),
            String::class.java
        )

        //val response : ResponseEntity<String> = restTemplate.getForEntity("/API/joboffers/1122/value", String::class.java)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.hasBody())
        val regex = """"job offer value":"109[.,]20""".toRegex()
        assert(response.body?.contains(regex) ?: false)
    }

    @Test
    fun `it should raise an error not finding the job offer trying to compute its value`(){
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/joboffers/1000/value",
            HttpMethod.GET,
            HttpEntity<String>(headers),
            String::class.java
        )

        //val response : ResponseEntity<String> = restTemplate.getForEntity("/API/joboffers/1000/value", String::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
    }


    @Test
    fun `it should raise an error finding a job not having a professional attached`(){
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/joboffers/1111/value",
            HttpMethod.GET,
            HttpEntity<String>(headers),
            String::class.java
        )

        //val response : ResponseEntity<String> = restTemplate.getForEntity("/API/joboffers/1111/value", String::class.java)
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `it should retrieve history for a given job offer`(){
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/joboffers/history/joboffer/1111",
            HttpMethod.GET,
            HttpEntity<String>(headers),
            String::class.java
        )

        //val response : ResponseEntity<String> = restTemplate.getForEntity("/API/joboffers/history/joboffer/1111", String::class.java)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.hasBody())
        assert(response.body?.contains("\"state\":\"CREATED\",\"date\":\"2024-05-16T10:00:00\",\"note\":\"test action\",\"jobOfferId\":1111,\"professionalId\":null")?:false)
    }

    @Test
    fun `it should raise an error trying to retrieve history for a given job offer not finding it`(){
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/joboffers/history/joboffer/1000",
            HttpMethod.GET,
            HttpEntity<String>(headers),
            String::class.java
        )

        //val response : ResponseEntity<String> = restTemplate.getForEntity("/API/joboffers/history/joboffer/1000", String::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `it should retrieve history for a given customer`(){
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/joboffers/history/customer/1001",
            HttpMethod.GET,
            HttpEntity<String>(headers),
            String::class.java
        )

        //val response : ResponseEntity<String> = restTemplate.getForEntity("/API/joboffers/history/customer/1001", String::class.java)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.hasBody())
        assert(response.body?.contains("\"state\":\"CREATED\",\"date\":\"2024-05-16T10:00:00\",\"note\":\"test action\",\"jobOfferId\":1111,\"professionalId\":null")?:false)
    }

    @Test
    fun `it should raise an error trying to retrieve history for a given customer not finding it`(){
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/joboffers/history/customer/1000",
            HttpMethod.GET,
            HttpEntity<String>(headers),
            String::class.java
        )

        //val response : ResponseEntity<String> = restTemplate.getForEntity("/API/joboffers/history/customer/1000", String::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `it should retrieve history for a given professional`(){
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/joboffers/history/professional/1404",
            HttpMethod.GET,
            HttpEntity<String>(headers),
            String::class.java
        )

        //val response : ResponseEntity<String> = restTemplate.getForEntity("/API/joboffers/history/professional/1404", String::class.java)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.hasBody())
        assert(response.body?.contains("\"state\":\"CONSOLIDATED\",\"date\":\"2024-05-16T10:00:00\",\"note\":\"test action\",\"jobOfferId\":1122,\"professionalId\":1404")?:false)
    }

    @Test
    fun `it should raise an error trying to retrieve history for a given professional not finding it`(){
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/joboffers/history/professional/1000",
            HttpMethod.GET,
            HttpEntity<String>(headers),
            String::class.java
        )

        //val response : ResponseEntity<String> = restTemplate.getForEntity("/API/joboffers/history/professional/1000", String::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `it should correctly update job offer's information`(){
        val jobOffer = JobOfferDTO(null, "testDesc2", "testSkills2", 2, null, 1.2, null, null)
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(jobOffer, headers)
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/joboffers/1222",
            HttpMethod.PUT,
            entity,
            String::class.java
        )

        //val response : ResponseEntity<String> = restTemplate.exchange("/API/joboffers/1222", HttpMethod.PUT, HttpEntity(jobOffer) , String::class.java)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.body?.contains("\"id\":1222,\"description\":\"testDesc2\",\"requiredSkills\":\"testSkills2\",\"duration\":2,\"status\":\"candidate_proposal\",\"profitMargin\":1.2,\"customer\":{\"id\":1001,\"name\":\"Mario\",\"surname\":\"Rossi\",\"category\":\"customer\",\"ssncode\":\"123456789\",\"mailList\":[{\"id\":1001,\"mail\":\"mariorossi@gmail.com\"}],\"addressList\":[],\"telephoneList\":[],\"professionalInfo\":null},\"professionalId\":null")?:false)
    }

    @Test
    fun `it should raise an error trying to update job offer's information not finding it`(){
        val jobOffer = JobOfferDTO(null, "testDesc2", "testSkills2", 2, null, 1.2, null, null)
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(jobOffer, headers)
        val response: ResponseEntity<String> = restTemplate.exchange(
            "/API/joboffers/1000",
            HttpMethod.PUT,
            entity,
            String::class.java
        )

        //val response : ResponseEntity<String> = restTemplate.exchange("/API/joboffers/1000", HttpMethod.PUT, HttpEntity(jobOffer) , String::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
    }



}
