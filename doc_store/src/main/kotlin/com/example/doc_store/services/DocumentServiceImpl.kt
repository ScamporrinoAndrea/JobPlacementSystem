package com.example.doc_store.services

import com.example.doc_store.entities.DocumentData
import com.example.doc_store.entities.DocumentMetadata
import com.example.doc_store.exceptionhandler.DocumentNotFoundException
import com.example.doc_store.exceptionhandler.DuplicateDocumentException
import com.example.doc_store.exceptionhandler.InvalidFileNameException
import com.example.doc_store.models.DocumentDataDTO
import com.example.doc_store.models.DocumentMetadataDTO
import com.example.doc_store.models.toDTO
import com.example.doc_store.repositories.DocumentDataRepo
import com.example.doc_store.repositories.DocumentMetadataRepo
import jakarta.transaction.Transactional
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
@Transactional
class DocumentServiceImpl(private val metadataRepo : DocumentMetadataRepo, private val dataRepo : DocumentDataRepo)  : DocumentService {

    override fun createDocument(metadata: DocumentMetadataDTO, content: ByteArray) {
        val name = metadata.name ?: throw InvalidFileNameException("The file uploaded has an invalid name")
        if(metadataRepo.findByName(name) != null){
            throw DuplicateDocumentException("File with name $name already exists")
        }
        if(metadata.jobId != null && metadataRepo.findByJobId(metadata.jobId) != null) {
            throw DuplicateDocumentException("File attached to Job ${metadata.jobId} already exists")
        }
        if(metadata.contactId != null && metadataRepo.findByContactId(metadata.contactId) != null) {
            throw DuplicateDocumentException("File attached to Contact ${metadata.contactId} already exists")
        }

        val docMetadata = if ((metadata.jobId != null) xor (metadata.contactId != null)) {
            DocumentMetadata(name, metadata.size, metadata.contentType, metadata.jobId, metadata.contactId, 0, LocalDateTime.now(), LocalDateTime.now())
        }
        else{
            throw InvalidFileNameException("The file uploaded has not a valid owner")
        }
        val data = DocumentData(content, docMetadata)
        docMetadata.document = data
        metadataRepo.save(docMetadata)

    }

    override fun getDocuments(pageNumber: Int?, limit: Int?): Map<String, List<DocumentMetadataDTO>> {
        return if(pageNumber == null || limit == null)
            mapOf("documents" to metadataRepo.findAll().map{it.toDTO()})
        else
            mapOf("documents" to metadataRepo.findAll(PageRequest.of(pageNumber-1, limit)).toList().map { it.toDTO() })
    }

    override fun getMetadataById(id: Long, type: String?): DocumentMetadataDTO? {
        val metadata = getMetadata(id, type)
        return metadata.toDTO()
    }

    override fun getDataById(id: Long, type: String?): DocumentDataDTO? {
        val metadata = getMetadata(id, type)
        return metadata.document?.toDTO()
    }

    override fun updateDocument(id: Long, metadata: DocumentMetadataDTO, content: ByteArray): DocumentMetadataDTO? {
        val existingDocument = metadataRepo.findById(id).orElse(null)
        existingDocument ?: throw DocumentNotFoundException("Document $id not found")

        val newName = metadata.name ?: throw InvalidFileNameException("The file uploaded has an invalid name")
        val tmp = metadataRepo.findByName(newName)
        if(tmp != null && tmp.id != id){
            throw DuplicateDocumentException("File with name $newName already exists")
        }

        existingDocument.document = DocumentData(content, existingDocument, existingDocument.document?.id)
        existingDocument.apply{
            name = newName
            size = metadata.size
            contentType = metadata.contentType
            jobId = existingDocument.jobId
            contactId = existingDocument.contactId
            version = existingDocument.version+1
            created = existingDocument.created
            lastModified = LocalDateTime.now()
        }
        dataRepo.save(existingDocument.document!!)
        metadataRepo.save(existingDocument)

        return existingDocument.toDTO()

    }

    override fun deleteDocument(id: Long) {
        if(!metadataRepo.existsById(id)) {
            throw DocumentNotFoundException("Document $id not found")
        }

        metadataRepo.deleteById(id)
    }

    fun getMetadata(id: Long, type: String?): DocumentMetadata {
        return when (type) {
            null -> metadataRepo.findById(id).orElse(null) ?: throw DocumentNotFoundException("Document $id not found")
            "job" -> metadataRepo.findByJobId(id) ?: throw DocumentNotFoundException("Document $id not found")
            else -> metadataRepo.findByContactId(id) ?: throw DocumentNotFoundException("Document $id not found")
        }
    }
}

