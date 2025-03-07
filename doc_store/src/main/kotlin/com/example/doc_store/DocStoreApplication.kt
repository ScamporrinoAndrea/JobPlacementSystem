package com.example.doc_store

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DocStoreApplication

fun main(args: Array<String>) {
	runApplication<DocStoreApplication>(*args)
}
