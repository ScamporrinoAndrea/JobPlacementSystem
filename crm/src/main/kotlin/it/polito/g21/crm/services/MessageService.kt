package it.polito.g21.crm.services

import it.polito.g21.crm.models.ActionOnMessageDTO
import it.polito.g21.crm.models.MessageDTO
import it.polito.g21.crm.models.StateWithMessageDTO
import it.polito.g21.crm.repositories.ActionOnMessageRepo
import it.polito.g21.crm.utils.PriorityValue
import java.time.LocalDate

interface MessageService {
    fun createMessage(message : MessageDTO)

    fun getMessages(pageNumber: Int?, limit: Int?, date: LocalDate?, subject: String?, body: String?,
                    channel: String?, state: String?, priority: Int?, email: String?,
                    city: String?, country: String?, street: String?, telephone: String?) : List<MessageDTO>

    fun getMessageById(id : Long) : MessageDTO?

    fun changeState(id: Long, dto : StateWithMessageDTO) : MessageDTO?

    fun getHistory(id : Long) : List<ActionOnMessageDTO>

    fun updatePriority(id : Long, newValue: Int)
}