package it.polito.g21.crm.models

import it.polito.g21.crm.entities.Contact
import it.polito.g21.crm.entities.Message
import it.polito.g21.crm.utils.ChannelType
import it.polito.g21.crm.utils.MachineStateType
import it.polito.g21.crm.utils.PriorityValue
import java.time.LocalDate

data class MessageDTO(
    val id: Long?,
    val sender: String,
    val date : LocalDate?,
    val subject : String?,
    val body : String?,
    val channel : String,
    val state : String?,
    val priority : Int?,

){
    val relatedContacts : MutableList<GeneralContactDTO> = mutableListOf()
}


fun Message.toDTO() : MessageDTO {
    val sender = when(this.channel){
        ChannelType.PHONE_CALL, ChannelType.TEXT_MESSAGE -> this.telephoneSender?.number.toString()
        ChannelType.EMAIL -> this.emailSender?.mail.toString()
        else -> this.addressSender?.toString().toString()
    }
    return MessageDTO(this.getId(), sender, this.date,
        this.subject,this.body,this.channel.value,this.state.value,this.priority.value)
}