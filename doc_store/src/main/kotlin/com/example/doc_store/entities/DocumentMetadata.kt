package com.example.doc_store.entities

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class DocumentMetadata(
    var name: String,
    var size: Long,
    var contentType: String?,
    var jobId: Long?,
    var contactId: Long?,
    var version: Long,
    var created: LocalDateTime,
    var lastModified: LocalDateTime,
    @OneToOne(mappedBy = "metadata", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var document: DocumentData? = null,
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null
) {
    // No-argument constructor required by JPA
    constructor() : this("", 0L, null, null, null, 0, LocalDateTime.now(), LocalDateTime.now())
}