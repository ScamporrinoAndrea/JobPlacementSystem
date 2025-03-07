package com.example.doc_store.models

import com.example.doc_store.entities.DocumentData

data class DocumentDataDTO (
    val name: String,
    val contentType: String?,
    val bytes: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DocumentDataDTO

        return bytes.contentEquals(other.bytes)
    }

    override fun hashCode(): Int {
        return bytes.contentHashCode()
    }
}

fun DocumentData.toDTO() : DocumentDataDTO =
    DocumentDataDTO(this.metadata.name, this.metadata.contentType, this.bytes)