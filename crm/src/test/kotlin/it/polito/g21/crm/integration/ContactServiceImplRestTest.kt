package it.polito.g21.crm.integration

import it.polito.g21.crm.entities.Contact
import it.polito.g21.crm.models.*
import it.polito.g21.crm.utils.CategoryType
import jakarta.annotation.PostConstruct
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.core.io.Resource
import org.springframework.http.*
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.annotation.Rollback
import java.net.URI
import java.time.LocalDate

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Transactional
class ContactServiceImplRestTest : IntegrationTest() {
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
    fun `it should retrieve all contacts`() {
        val response: ResponseEntity<String> = restTemplate.exchange("/API/contacts/", HttpMethod.GET, HttpEntity<String>(headers), String::class.java)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.hasBody())
        assert(response.body?.contains("\"contacts\":[{\"id\":1001,\"name\":\"Mario\",\"surname\":\"Rossi\",\"category\":\"customer\",\"ssncode\":\"123456789\",\"mailList\":[{\"id\":1001,\"mail\":\"mariorossi@gmail.com\"}],\"addressList\":[],\"telephoneList\":[],\"professionalInfo\":null},{\"id\":1002,\"name\":\"Giacomo\",\"surname\":\"Verdi\",\"category\":\"customer\",\"ssncode\":\"123456788\",\"mailList\":[],\"addressList\":[],\"telephoneList\":[],\"professionalInfo\":null},{\"id\":1003,\"name\":\"Mario\",\"surname\":\"Giallo\",\"category\":\"professional\",\"ssncode\":\"123456787\",\"mailList\":[],\"addressList\":[],\"telephoneList\":[],\"professionalInfo\":{\"skills\":\"backend\",\"employmentState\":\"available\",\"location\":\"Italy\",\"dailyRate\":2.4,\"linkedJobs\":[{\"id\":1211,\"description\":\"aborted job offer\",\"requiredSkills\":\"frontend\",\"duration\":12,\"status\":\"aborted\",\"profitMargin\":2.9,\"customer\":{\"id\":1001,\"name\":\"Mario\",\"surname\":\"Rossi\",\"category\":\"customer\",\"ssncode\":\"123456789\",\"mailList\":[{\"id\":1001,\"mail\":\"mariorossi@gmail.com\"}],\"addressList\":[],\"telephoneList\":[],\"professionalInfo\":null},\"professionalId\":1003}]}},{\"id\":1004,\"name\":\"Luca\",\"surname\":\"Nero\",\"category\":\"unknown\",\"ssncode\":\"123456786\",\"mailList\":[],\"addressList\":[],\"telephoneList\":[],\"professionalInfo\":null},{\"id\":1203,\"name\":\"Andrea\",\"surname\":\"Verde\",\"category\":\"professional\",\"ssncode\":\"123456787\",\"mailList\":[],\"addressList\":[],\"telephoneList\":[],\"professionalInfo\":{\"skills\":\"frontend\",\"employmentState\":\"available\",\"location\":\"Italy\",\"dailyRate\":3.2,\"linkedJobs\":[]}},{\"id\":1204,\"name\":\"Andrea\",\"surname\":\"Bianchino\",\"category\":\"professional\",\"ssncode\":\"123456787\",\"mailList\":[],\"addressList\":[],\"telephoneList\":[],\"professionalInfo\":{\"skills\":\"frontend\",\"employmentState\":\"available\",\"location\":\"Italy\",\"dailyRate\":3.0,\"linkedJobs\":[]}},{\"id\":1304,\"name\":\"Giuseppe\",\"surname\":\"Viola\",\"category\":\"professional\",\"ssncode\":\"123456787\",\"mailList\":[],\"addressList\":[],\"telephoneList\":[],\"professionalInfo\":{\"skills\":\"frontend\",\"employmentState\":\"available\",\"location\":\"Italy\",\"dailyRate\":3.0,\"linkedJobs\":[]}},{\"id\":1404,\"name\":\"Giuseppe\",\"surname\":\"Sasso\",\"category\":\"professional\",\"ssncode\":\"123456787\",\"mailList\":[],\"addressList\":[],\"telephoneList\":[],\"professionalInfo\":{\"skills\":\"frontend\",\"employmentState\":\"employed\",\"location\":\"Italy\",\"dailyRate\":4.2,\"linkedJobs\":[{\"id\":1122,\"description\":\"good job offer\",\"requiredSkills\":\"backend\",\"duration\":13,\"status\":\"consolidated\",\"profitMargin\":2.0,\"customer\":{\"id\":1001,\"name\":\"Mario\",\"surname\":\"Rossi\",\"category\":\"customer\",\"ssncode\":\"123456789\",\"mailList\":[{\"id\":1001,\"mail\":\"mariorossi@gmail.com\"}],\"addressList\":[],\"telephoneList\":[],\"professionalInfo\":null},\"professionalId\":1404},{\"id\":1123,\"description\":\"that is a good job\",\"requiredSkills\":\"backend\",\"duration\":11,\"status\":\"done\",\"profitMargin\":2.9,\"customer\":{\"id\":1001,\"name\":\"Mario\",\"surname\":\"Rossi\",\"category\":\"customer\",\"ssncode\":\"123456789\",\"mailList\":[{\"id\":1001,\"mail\":\"mariorossi@gmail.com\"}],\"addressList\":[],\"telephoneList\":[],\"professionalInfo\":null},\"professionalId\":1404}]}},{\"id\":5003,\"name\":\"Professional\",\"surname\":\"Test\",\"category\":\"professional\",\"ssncode\":\"123456787\",\"mailList\":[{\"id\":1002,\"mail\":\"test@mail.com\"}],\"addressList\":[{\"id\":1002,\"address\":\"Street Test, 11, Turin, Italy\"}],\"telephoneList\":[{\"id\":1001,\"number\":\"3568899777\"}],\"professionalInfo\":{\"skills\":\"frontend\",\"employmentState\":\"employed\",\"location\":\"Italy\",\"dailyRate\":4.2,\"linkedJobs\":[]}}]") ?: false)

    }

    @Test
    fun `it should retrieve all contacts with filters`(){
        val response: ResponseEntity<String> = restTemplate.exchange("/API/contacts/?name=Mario&surname=Rossi", HttpMethod.GET, HttpEntity<String>(headers), String::class.java)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.hasBody())
        assert(response.body?.contains("\"id\":1001,\"name\":\"Mario\",\"surname\":\"Rossi\",\"category\":\"customer\",\"ssncode\":\"123456789\",\"mailList\":[{\"id\":1001,\"mail\":\"mariorossi@gmail.com\"}],\"addressList\":[],\"telephoneList\":[],\"professionalInfo\":null") ?: false)
    }

    @Test
    fun `it should retrieve a contact specifying the id`() {
        val response: ResponseEntity<String> = restTemplate.exchange("/API/contacts/1001", HttpMethod.GET, HttpEntity<String>(headers), String::class.java)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.hasBody())
        assert(response.body?.contains("\"id\":1001,\"name\":\"Mario\",\"surname\":\"Rossi\",\"category\":\"customer\",\"ssncode\":\"123456789\",\"mailList\":[{\"id\":1001,\"mail\":\"mariorossi@gmail.com\"}],\"addressList\":[],\"telephoneList\":[],\"professionalInfo\":null") ?: false)
    }

    @Test
    fun `it should raise an error not finding the Contact`() {
        val response: ResponseEntity<String> = restTemplate.exchange("/API/contacts/1000", HttpMethod.GET, HttpEntity<String>(headers), String::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `it should create a contact entity`(){
        headers.contentType = MediaType.APPLICATION_JSON
        val contact = GeneralContactDTO(null,"Edoardo", "Morello", "unknown" , "123456789", null)
        val entity = HttpEntity(contact, headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/contacts/", HttpMethod.POST, entity, String::class.java)
        assert(response.statusCode == HttpStatus.CREATED)
    }

    @Test
    fun `it should correctly update the category into professional`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val professionalInfo = ProfessionalDTO("frontend","employed","Italy", 5.6,null)
        val entity = HttpEntity(professionalInfo, headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/contacts/5003/category/?value=professional",
            HttpMethod.PUT, entity , String::class.java)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.body?.contains("\"id\":5003,\"name\":\"Professional\",\"surname\":\"Test\",\"category\":\"professional\",\"ssncode\":\"123456787\",\"professionalInfo\":{\"skills\":\"frontend\",\"employmentState\":\"employed\",\"location\":\"Italy\",\"dailyRate\":4.2,\"linkedJobs\":[]}") ?: false)

    }

    @Test
    fun `it should correctly update the category into customer`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val voidProfessionalInfo = ProfessionalDTO("null","null","null", 0.0,null)
        val entity = HttpEntity(voidProfessionalInfo, headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/contacts/5003/category/?value=customer",
            HttpMethod.PUT, entity , String::class.java)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.body?.contains("\"id\":5003,\"name\":\"Professional\",\"surname\":\"Test\",\"category\":\"customer\",\"ssncode\":\"123456787\",\"professionalInfo\":null") ?: false)
    }


    @Test
    fun `it should correctly update the category into unknown`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val voidProfessionalInfo = ProfessionalDTO("null","null","null", 0.0,null)
        val entity = HttpEntity(voidProfessionalInfo, headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/contacts/5003/category/?value=unknown",
            HttpMethod.PUT, entity , String::class.java)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.body?.contains("\"id\":5003,\"name\":\"Professional\",\"surname\":\"Test\",\"category\":\"unknown\",\"ssncode\":\"123456787\",\"professionalInfo\":null") ?: false)
    }

    @Test
    fun `it should raise an error not finding the Contact trying to change the category`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val voidProfessionalInfo = ProfessionalDTO("null","null","null", 0.0,null)
        val entity = HttpEntity(voidProfessionalInfo, headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/contacts/1020/category/?value=unknown",
            HttpMethod.PUT, entity , String::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `it should raise an error finding an invalid emp state trying to change contact's category`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val professionalInfo = ProfessionalDTO("frontend","INVALID","Italy", 5.6,null)
        val entity = HttpEntity(professionalInfo, headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/contacts/1001/category/?value=professional",
            HttpMethod.PUT, entity , String::class.java)
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `it should correctly delete a Contact`() {
        val requestEntity = RequestEntity<Any>(headers, HttpMethod.DELETE, URI("/API/contacts/1002"))
        val response: ResponseEntity<String> = restTemplate.exchange(requestEntity, String::class.java)
        assert(response.statusCode == HttpStatus.NO_CONTENT)
    }

    @Test
    fun `it should raise an error on deleting a Contact that not exists`() {
        val requestEntity = RequestEntity<Any>(headers, HttpMethod.DELETE, URI("/API/contacts/1000"))
        val response: ResponseEntity<String> = restTemplate.exchange(requestEntity, String::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `it should add a customer note`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val note = NoteDTO("test note", LocalDate.now())
        val entity = HttpEntity(note, headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/customers/1002/notes", HttpMethod.POST, entity , String::class.java)
        assert(response.statusCode == HttpStatus.CREATED)
    }

    @Test
    fun `it should raise an error in adding a customer note to a customer that does not exist`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val note = NoteDTO("test note", LocalDate.now())
        val entity = HttpEntity(note, headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/customers/1000/notes", HttpMethod.POST , entity , String::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `it should raise an error in adding a customer note to a non-customer`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val note = NoteDTO("test note", LocalDate.now())
        val entity = HttpEntity(note, headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/customers/1003/notes", HttpMethod.POST, entity, String::class.java)
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `it should retrieve customer note`() {
        val response : ResponseEntity<String> = restTemplate.exchange("/API/customers/1001/notes", HttpMethod.GET, HttpEntity<String>(headers), String::class.java)
        assert(response.statusCode == HttpStatus.OK)
        assert(response.body?.contains("\"note\":\"example note\",\"date\":\"2024-05-16\"")?:false)
    }

    @Test
    fun `it should raise an error in retrieve a customer note of a customer that does not exist`() {
        val response : ResponseEntity<String> = restTemplate.exchange("/API/customers/1000/notes", HttpMethod.GET, HttpEntity<String>(headers), String::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `it should raise an error in retrieve a customer note of a non-customer`() {
        val response : ResponseEntity<String> = restTemplate.exchange("/API/customers/1003/notes", HttpMethod.GET, HttpEntity<String>(headers), String::class.java)
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
    }

    ///---------mail,address,telephone

    @Test
    fun `it should correctly add a new mail`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val mail = EmailDTO(null, "test@test.com")
        val entity = HttpEntity(mail, headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/contacts/1001/email", HttpMethod.POST, entity , String::class.java)
        assert(response.statusCode == HttpStatus.CREATED)
    }


    @Test
    fun `it should raise an error adding a new mail`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val mail = EmailDTO(null, "testtest.com")
        val entity = HttpEntity(mail, headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/contacts/1001/email", HttpMethod.POST, entity , String::class.java)
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
    }


    @Test
    fun `it should raise an error not finding the Contact trying to add a new mail`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val mail = EmailDTO(null, "test@test.com")
        val entity = HttpEntity(mail, headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/contacts/1000/email", HttpMethod.POST, entity , String::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `it should raise an error due to the fact that the mail is already linked trying to add a new mail`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val mail = EmailDTO(null, "mariorossi@gmail.com")
        val entity = HttpEntity(mail, headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/contacts/1001/email", HttpMethod.POST, entity , String::class.java)
        assert(response.statusCode == HttpStatus.NOT_MODIFIED)
    }

    @Test
    fun `it should correctly update a mail`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val mail = EmailDTO(null, "test@test.com")
        val entity = HttpEntity(mail, headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/contacts/1001/email/1001",HttpMethod.PUT, entity , String::class.java)
        assert(response.statusCode == HttpStatus.OK)
        println(response.body)
        assert(response.body?.contains("\"id\":1001,\"name\":\"Mario\",\"surname\":\"Rossi\",\"category\":\"customer\",\"ssncode\":\"123456789\",\"mailList\":[{\"id\":3,\"mail\":\"test@test.com\"}],\"addressList\":[],\"telephoneList\":[],\"professionalInfo\":null")?:false)
    }

    @Test
    fun `it should raise an error not finding the Contact trying to update a mail`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val mail = EmailDTO(null, "test@test.com")
        val entity = HttpEntity(mail, headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/contacts/1000/email/1001",HttpMethod.PUT, entity , String::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `it should raise an error due to the fact that the mail is already linked trying to update a mail`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val mail = EmailDTO(null, "mariorossi@gmail.com")
        val entity = HttpEntity(mail, headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/contacts/1001/email/1001",HttpMethod.PUT, entity , String::class.java)
        assert(response.statusCode == HttpStatus.NOT_MODIFIED)
    }

    @Test
    fun `it should raise an error due to the fact that the mail is not found trying to update a mail`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val mail = EmailDTO(null, "test@test.com")
        val entity = HttpEntity(mail, headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/contacts/1001/email/1000",HttpMethod.PUT, entity , String::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `it should correctly delete an email`() {
        val requestEntity = RequestEntity<Any>(headers, HttpMethod.DELETE, URI("/API/contacts/1001/email/1001"))
        val response: ResponseEntity<String> = restTemplate.exchange(requestEntity, String::class.java)
        assert(response.statusCode == HttpStatus.NO_CONTENT)
    }

    @Test
    fun `it should raise an error not finding the Contact trying to delete a mail`() {
        val requestEntity = RequestEntity<Any>(headers, HttpMethod.DELETE, URI("/API/contacts/1000/email/1001"))
        val response: ResponseEntity<String> = restTemplate.exchange(requestEntity, String::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `it should raise an error due to the fact that the mail is not found trying to delete a mail`() {
        val requestEntity = RequestEntity<Any>(headers, HttpMethod.DELETE, URI("/API/contacts/1001/email/1000"))
        val response: ResponseEntity<String> = restTemplate.exchange(requestEntity, String::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `it should correctly add a new address`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val address =  AddressDTO(null, "testName, 1, testCity, testCountry")
        val entity = HttpEntity(address, headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/contacts/5003/address", HttpMethod.POST, entity, String::class.java)
        assert(response.statusCode == HttpStatus.CREATED)
    }

    @Test
    fun `it should raise an error adding a new address`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val address =  AddressDTO(null, "testName 1, testCity, testCountry")
        val entity = HttpEntity(address, headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/contacts/5003/address", HttpMethod.POST, entity , String::class.java)
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `it should raise an error not finding the Contact trying to add a new address`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val address =  AddressDTO(null, "testName, 1, testCity, testCountry")
        val entity = HttpEntity(address, headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/contacts/100000/address", HttpMethod.POST, entity, String::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `it should raise an error due to the fact that the address is already linked trying to add a new address`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val address =  AddressDTO(null, "Street Test, 11, Turin, Italy")
        val entity = HttpEntity(address, headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/contacts/5003/address", HttpMethod.POST, entity, String::class.java)
        assert(response.statusCode == HttpStatus.NOT_MODIFIED)
    }

    @Test
    fun `it should correctly update an address`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val address =  AddressDTO(null, "Street, 12, Turin, Italy")
        val entity = HttpEntity(address, headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/contacts/5003/address/1002",
            HttpMethod.PUT, entity , String::class.java)
        assert(response.statusCode == HttpStatus.OK)
        println("Body: ${response.body}")
        assert(response.body?.contains("\"id\":5003,\"name\":\"Professional\",\"surname\":\"Test\",\"category\":\"professional\",\"ssncode\":\"123456787\",\"mailList\":[{\"id\":1002,\"mail\":\"test@mail.com\"}],\"addressList\":[{\"id\":1,\"address\":\"Street, 12, Turin, Italy\"}],\"telephoneList\":[{\"id\":1001,\"number\":\"3568899777\"}],\"professionalInfo\":{\"skills\":\"frontend\",\"employmentState\":\"employed\",\"location\":\"Italy\",\"dailyRate\":4.2,\"linkedJobs\":[]}") ?: false)
    }

    @Test
    fun `it should raise an error not finding the Contact trying to update an address`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val address =  AddressDTO(null, "Street, 12, Turin, Italy")
        val entity = HttpEntity(address, headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/contacts/12333/address/1001",
            HttpMethod.PUT, entity , String::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `it should raise an error due to the fact that the address is already linked trying to update an address`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val address =  AddressDTO(null, "Street Test, 11, Turin, Italy")
        val entity = HttpEntity(address, headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/contacts/5003/address/1002",
            HttpMethod.PUT, entity , String::class.java)
        assert(response.statusCode == HttpStatus.NOT_MODIFIED)
    }

    @Test
    fun `it should raise an error due to the fact that the address is not found trying to update an address`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val address =  AddressDTO(null, "Street, 12, Turin, Italy")
        val entity = HttpEntity(address, headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/contacts/12333/address/10011",
            HttpMethod.PUT, entity , String::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `it should correctly delete an address`() {
        val requestEntity = RequestEntity<Any>(headers, HttpMethod.DELETE, URI("/API/contacts/5003/address/1002"))
        val response: ResponseEntity<String> = restTemplate.exchange(requestEntity, String::class.java)
        assert(response.statusCode == HttpStatus.NO_CONTENT)
    }

    @Test
    fun `it should raise an error not finding the Contact trying to delete a address`() {
        val requestEntity = RequestEntity<Any>(headers, HttpMethod.DELETE, URI("/API/contacts/10022/address/1001"))
        val response: ResponseEntity<String> = restTemplate.exchange(requestEntity, String::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `it should raise an error due to the fact that the address is not found trying to delete a address`() {
        val requestEntity = RequestEntity<Any>(headers, HttpMethod.DELETE, URI("/API/contacts/5003/address/10011"))
        val response: ResponseEntity<String> = restTemplate.exchange(requestEntity, String::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `it should correctly add a new telephone`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val telephone = TelephoneDTO(null, "1234567890")
        val entity = HttpEntity(telephone, headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/contacts/5003/telephone", HttpMethod.POST, entity , String::class.java)
        assert(response.statusCode == HttpStatus.CREATED)
    }

    @Test
    fun `it should raise an error adding a new telephone`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val telephone = TelephoneDTO(null, "12345678C0")
        val entity = HttpEntity(telephone, headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/contacts/5003/telephone", HttpMethod.POST, entity , String::class.java)
        assert(response.statusCode == HttpStatus.BAD_REQUEST)
    }

    @Test
    fun `it should raise an error not finding the Contact trying to add a new telephone`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val telephone = TelephoneDTO(null, "3568899777")
        val entity = HttpEntity(telephone, headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/contacts/1000/telephone", HttpMethod.POST, entity , String::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `it should raise an error due to the fact that the telephone is already linked trying to add a new telephone`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val telephone = TelephoneDTO(null, "3568899777")
        val entity = HttpEntity(telephone, headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/contacts/5003/telephone", HttpMethod.POST, entity , String::class.java)
        assert(response.statusCode == HttpStatus.NOT_MODIFIED)
    }

    @Test
    fun `it should correctly update a telephone`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val telephone = TelephoneDTO(null, "1234567890")
        val entity = HttpEntity(telephone, headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/contacts/5003/telephone/1001", HttpMethod.PUT, entity , String::class.java)
        assert(response.statusCode == HttpStatus.OK)
    }

    @Test
    fun `it should raise an error not finding the Contact trying to update a telephone`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val telephone = TelephoneDTO(null, "1234567890")
        val entity = HttpEntity(telephone, headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/contacts/1000/telephone/1001", HttpMethod.PUT, entity , String::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `it should raise an error due to the fact that the telephone is already linked trying to update a telephone`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val telephone = TelephoneDTO(null, "3568899777")
        val entity = HttpEntity(telephone, headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/contacts/5003/telephone/1001", HttpMethod.PUT, entity , String::class.java)
        assert(response.statusCode == HttpStatus.NOT_MODIFIED)
    }

    @Test
    fun `it should raise an error due to the fact that the telephone is not found trying to update a telephone`() {
        headers.contentType = MediaType.APPLICATION_JSON
        val telephone = TelephoneDTO(null, "1234567890")
        val entity = HttpEntity(telephone, headers)
        val response : ResponseEntity<String> = restTemplate.exchange("/API/contacts/5003/telephone/1000", HttpMethod.PUT, entity , String::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `it should correctly delete an telephone`() {
        val requestEntity = RequestEntity<Any>(headers, HttpMethod.DELETE, URI("/API/contacts/5003/telephone/1001"))
        val response: ResponseEntity<String> = restTemplate.exchange(requestEntity, String::class.java)
        assert(response.statusCode == HttpStatus.NO_CONTENT)
    }

    @Test
    fun `it should raise an error not finding the Contact trying to delete a telephone`() {
        val requestEntity = RequestEntity<Any>(headers, HttpMethod.DELETE, URI("/API/contacts/1000/telephone/1001"))
        val response: ResponseEntity<String> = restTemplate.exchange(requestEntity, String::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
    }

    @Test
    fun `it should raise an error due to the fact that the telephone is not found trying to delete a telephone`() {
        val requestEntity = RequestEntity<Any>(headers, HttpMethod.DELETE, URI("/API/contacts/5003/telephone/1000"))
        val response: ResponseEntity<String> = restTemplate.exchange(requestEntity, String::class.java)
        assert(response.statusCode == HttpStatus.NOT_FOUND)
    }



}