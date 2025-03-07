package com.example.doc_store.controllers

import com.example.doc_store.LoggerConfig
import com.example.doc_store.models.DocumentMetadataDTO
import com.example.doc_store.services.DocumentService
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import java.util.logging.Logger

@Validated
@RestController
@RequestMapping("/API/documents")
class DocumentController(val mainService: DocumentService, val logger: LoggerConfig) {
    @GetMapping("/", "")
    @PreAuthorize("hasRole('manager') or hasRole('guest') or hasRole('operator')")
    fun getAllDocuments(@Valid @RequestParam("pageNumber") @Min(1) pageNumber: Int?,
                        @Valid @RequestParam("limit") @Min(0) limit: Int?) : Map<String,List<DocumentMetadataDTO>>{
        return mainService.getDocuments(pageNumber, limit)
    }

    @GetMapping("{documentId}/", "{documentId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('manager') or hasRole('guest') or hasRole('operator')")
    fun getMetadataById(@PathVariable("documentId") id: Long) : DocumentMetadataDTO?{
        return mainService.getMetadataById(id, null)
    }

    @GetMapping("{metadataId}/data/", "{metadataId}/data")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('manager') or hasRole('guest') or hasRole('operator')")
    fun getDataById(@PathVariable("metadataId") id: Long) : ResponseEntity<ByteArrayResource> {
        val res = mainService.getDataById(id, null)
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${res?.name}\"")
            .header(HttpHeaders.CONTENT_TYPE, res?.contentType)
            .body(res?.bytes?.let { ByteArrayResource(it) })
    }

    @PostMapping("/", "")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('manager') or hasRole('operator')")
    fun insertNewDocument(@RequestParam("file") file: MultipartFile,
                          @RequestParam("jobId") jobId: Long?,
                          @RequestParam("contactId") contactId: Long?){
        val metadata = DocumentMetadataDTO(null, file.originalFilename, file.size, file.contentType, jobId, contactId, 0, LocalDateTime.now(), LocalDateTime.now())
        mainService.createDocument(metadata, file.bytes)
        logger.info("Document created successfully")
    }

    @PutMapping("{metadataId}/", "{metadataId}")
    @PreAuthorize("hasRole('manager') or hasRole('operator')")
    fun updateDocument(@PathVariable("metadataId") id: Long, @RequestParam("file") file: MultipartFile) : DocumentMetadataDTO? {
        val metadata = DocumentMetadataDTO(null, file.originalFilename, file.size, file.contentType, null, null, 0, LocalDateTime.now(), LocalDateTime.now())
        val res = mainService.updateDocument(id, metadata, file.bytes)
        logger.info("Document updated successfully")
        return res
    }

    @DeleteMapping("{metadataId}/", "{metadataId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('manager') or hasRole('operator')")
    fun deleteDocument(@PathVariable("metadataId") id: Long){
        mainService.deleteDocument(id)
        logger.info("Document deleted successfully")
    }

    @GetMapping("job/{jobId}/", "job/{jobId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('manager') or hasRole('guest') or hasRole('operator')")
    fun getMetadataByJobId(@PathVariable("jobId") id: Long) : DocumentMetadataDTO?{
        return mainService.getMetadataById(id, "job")
    }

    @GetMapping("contact/{contactId}/", "contact/{contactId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('manager') or hasRole('guest') or hasRole('operator')")
    fun getMetadataByContactId(@PathVariable("contactId") id: Long) : DocumentMetadataDTO?{
        return mainService.getMetadataById(id, "contact")
    }

    @GetMapping("job/{jobId}/data/", "job/{jobId}/data")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('manager') or hasRole('guest') or hasRole('operator')")
    fun getJobDataById(@PathVariable("jobId") id: Long) : ResponseEntity<ByteArrayResource> {
        val res = mainService.getDataById(id, "job")
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${res?.name}\"")
            .header(HttpHeaders.CONTENT_TYPE, res?.contentType)
            .body(res?.bytes?.let { ByteArrayResource(it) })
    }

    @GetMapping("contact/{contactId}/data/", "contact/{contactId}/data")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('manager') or hasRole('guest') or hasRole('operator')")
    fun getContactDataById(@PathVariable("contactId") id: Long) : ResponseEntity<ByteArrayResource> {
        val res = mainService.getDataById(id, "contact")
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${res?.name}\"")
            .header(HttpHeaders.CONTENT_TYPE, res?.contentType)
            .body(res?.bytes?.let { ByteArrayResource(it) })
    }

}