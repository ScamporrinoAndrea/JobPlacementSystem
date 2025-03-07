package it.polito.g21.crm.entities

import it.polito.g21.crm.utils.MachineStateType
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
class ActionOnMessage (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id : Long? = null,
    var state : MachineStateType,
    var date : LocalDateTime,
    @ManyToOne
    var message : Message?= null,
    var comment : String?
    ) {}