package it.polito.g21.crm.entities

import it.polito.g21.crm.utils.ChannelType
import it.polito.g21.crm.utils.MachineStateType
import it.polito.g21.crm.utils.PriorityValue
import jakarta.persistence.*
import java.time.LocalDate

@Entity
class Message(
    @ManyToOne
    var telephoneSender : Telephone?=null,
    @ManyToOne
    var addressSender : Address?=null,
    @ManyToOne
    var emailSender: Email?=null,

    var date : LocalDate,
    var subject : String?,
    var body : String?,
    var channel : ChannelType,
    ) : EntityBase<Long>()  {
    var state : MachineStateType = MachineStateType.RECEIVED
    var priority : PriorityValue = PriorityValue.LOW

    @OneToMany(mappedBy = "message")
    val actions = mutableSetOf<ActionOnMessage>()

    fun addAction(a : ActionOnMessage){
        a.message = this
        actions.add(a)
    }
}