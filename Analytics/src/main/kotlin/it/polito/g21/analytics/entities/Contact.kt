package it.polito.g21.analytics.entities

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Contact(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id : Long? = null,
    var category : String,
    var day : Int,
    var month : Int,
    var year : Int
) {
}