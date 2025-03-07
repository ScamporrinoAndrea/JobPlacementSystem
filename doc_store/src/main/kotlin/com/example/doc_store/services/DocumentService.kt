package com.example.doc_store.services

import com.example.doc_store.models.DocumentDataDTO
import com.example.doc_store.models.DocumentMetadataDTO

interface DocumentService {
    fun createDocument(metadata: DocumentMetadataDTO, content: ByteArray)

    fun getDocuments(pageNumber: Int?, limit: Int?) : Map<String,List<DocumentMetadataDTO>>

    fun getMetadataById(id : Long, type: String?) : DocumentMetadataDTO?

    fun getDataById(id : Long, type: String?) : DocumentDataDTO?

    fun updateDocument(id : Long, metadata: DocumentMetadataDTO, content: ByteArray) : DocumentMetadataDTO?

    fun deleteDocument(id: Long)

}