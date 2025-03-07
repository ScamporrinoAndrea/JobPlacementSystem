package com.example.doc_store.repositories

import com.example.doc_store.entities.DocumentData
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DocumentDataRepo : JpaRepository<DocumentData, Long>