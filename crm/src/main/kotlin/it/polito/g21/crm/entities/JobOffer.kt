package it.polito.g21.crm.entities

import it.polito.g21.crm.utils.JobStatus
import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToOne

@Entity
class JobOffer(
    var description : String,
    var requiredSkills : String,
    var duration : Int, //in days
    var profitMargin : Double,
    @ManyToOne
    var customer: Customer? = null
) : EntityBase<Long>() {
    //todo: VALUE = DURATION*(PROFESSIONAL_RATE)*(PROFIT_MARGIN)

    //Many-to-one with professional
    @ManyToOne
    var professional: Professional? = null

    //todo: history one-to-many per tenere traccia di: cambio di stato, note, data e ora, professional

    var status = JobStatus.CREATED
}