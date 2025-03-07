package it.polito.g21.crm.entities

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.apache.camel.attachment.Attachment

class NewEmail(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id : Long? = null,
    var recipient : String,
    var subject: String,
    var body: String
)