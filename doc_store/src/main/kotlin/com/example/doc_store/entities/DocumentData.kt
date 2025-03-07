package com.example.doc_store.entities

import jakarta.persistence.*


@Entity
class DocumentData(
    var bytes: ByteArray,
    @OneToOne var metadata: DocumentMetadata,
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null
) {
    // No-argument constructor required by JPA
    constructor() : this(ByteArray(0), DocumentMetadata())
}
