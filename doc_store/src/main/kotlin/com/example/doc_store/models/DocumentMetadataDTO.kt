package com.example.doc_store.models

import com.example.doc_store.entities.DocumentMetadata
import java.time.LocalDateTime

data class DocumentMetadataDTO (
    val id: Long?,
    val name: String?,
    val size: Long,
    val contentType: String?,
    val jobId: Long?,
    val contactId: Long?,
    val version: Long,
    val created: LocalDateTime,
    val lastModified: LocalDateTime,
)

fun DocumentMetadata.toDTO() : DocumentMetadataDTO =
    DocumentMetadataDTO(this.id, this.name, this.size, this.contentType, this.jobId, this.contactId,
        this.version, this.created, this.lastModified)