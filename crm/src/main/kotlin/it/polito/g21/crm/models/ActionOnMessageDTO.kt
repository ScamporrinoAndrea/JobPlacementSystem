package it.polito.g21.crm.models

import it.polito.g21.crm.entities.ActionOnMessage
import it.polito.g21.crm.utils.MachineStateType
import java.time.LocalDateTime

data class ActionOnMessageDTO(
    val state : MachineStateType,
    val date : LocalDateTime?,
    val comment : String?
)

fun ActionOnMessage.toDTO() : ActionOnMessageDTO =
    ActionOnMessageDTO(this.state,this.date,this.comment)