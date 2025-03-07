package com.example.doc_store.repositories

import com.example.doc_store.entities.DocumentMetadata
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DocumentMetadataRepo : JpaRepository<DocumentMetadata, Long> {
    fun findByName(name: String): DocumentMetadata?
    fun findByJobId(jobId: Long): DocumentMetadata?
    fun findByContactId(contactId: Long): DocumentMetadata?
}