package it.polito.g21.analytics.entities

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class JobOffer(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id : Long? = null,
    var status : String,
    var jobId : Long,
    var day : Int,
    var month : Int,
    var year : Int
) {
}