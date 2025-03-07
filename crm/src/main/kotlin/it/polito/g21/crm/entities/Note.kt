package it.polito.g21.crm.entities

import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
class Note(
    val date: LocalDate,
    val message: String,
    ): EntityBase<Long>() {
        @ManyToOne
        var customer: Customer? = null

        @ManyToOne
        var professional: Professional? = null
}