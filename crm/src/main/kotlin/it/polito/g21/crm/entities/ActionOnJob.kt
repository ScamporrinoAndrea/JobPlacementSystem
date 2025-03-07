package it.polito.g21.crm.entities

import it.polito.g21.crm.utils.JobStatus
import it.polito.g21.crm.utils.MachineStateType
import jakarta.persistence.Entity
import jakarta.persistence.ManyToOne
import org.springframework.boot.autoconfigure.batch.BatchProperties.Job
import java.time.LocalDateTime

@Entity
class ActionOnJob(
    var state : JobStatus,
    var date : LocalDateTime,
    var note: String?,
    @ManyToOne
    var job : JobOffer?= null,
    @ManyToOne
    var professional: Professional?= null
) : EntityBase<Long>(){
}