package it.polito.g21.crm.services

import it.polito.g21.crm.entities.*
import it.polito.g21.crm.exceptionhandler.*
import it.polito.g21.crm.models.*
import it.polito.g21.crm.repositories.*
import it.polito.g21.crm.utils.*
import jakarta.transaction.Transactional
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
@Transactional
class MessageServiceImpl(private val messageRepo: MessageRepo, private val emailRepo : EmailRepo,
                         private val addressRepo: AddressRepo, private val telephoneRepo: TelephoneRepo,
                         private val actionOnMessageRepo: ActionOnMessageRepo
) : MessageService {
    override fun createMessage(message: MessageDTO) {
        val date = LocalDate.now()
        val ctype  = when (message.channel){
            "phone call" -> ChannelType.PHONE_CALL
            "text message" -> ChannelType.TEXT_MESSAGE
            "email" -> ChannelType.EMAIL
            "address" -> ChannelType.ADDRESS
            else -> ChannelType.OTHER
        }

        var regex = Regex("^\\+?\\d+$")

        val mEntity : Message = when(ctype){
            ChannelType.PHONE_CALL, ChannelType.TEXT_MESSAGE ->{
                // Regex per numero di telefono
                if(!regex.matches(message.sender)){
                    throw MismatchChannelException("You should provide a Telephone having chosen this channel type")
                }
                var res = telephoneRepo.findByTelephoneDetails(message.sender)
                if(res == null){
                    res = Telephone(null, message.sender)
                    telephoneRepo.save(res)
                }

                Message(res,null,null,date,
                    message.subject,message.body,ctype)

            }
            ChannelType.EMAIL-> {
                regex = Regex("[a-zA-Z0-9.]+@[a-zA-Z0-9.]+\\.[a-zA-Z]{2,}")
                if( !regex.matches(message.sender)){
                    throw MismatchChannelException("You should provide an Email having chosen this channel type")
                }
                var res = emailRepo.findByMail(message.sender)
                if(res == null){
                    res = Email(null, message.sender)
                    emailRepo.save(res)
                }

                Message(null,null,res,date,
                    message.subject,message.body,ctype)

            }
            else -> {
                regex = Regex("[a-zA-Z\\s]+,\\s*[a-zA-Z0-9]+,\\s[a-zA-Z\\s]+,\\s[a-zA-Z\\s]+")
                if( !regex.matches(message.sender)){
                    throw MismatchChannelException("You should provide an Address having chosen this channel type")
                }
                val parts = message.sender.split(",")
                var res = addressRepo.findByAddressDetails(
                        parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim())
                if(res == null){
                    res = Address(null, parts[1].trim(), parts[0].trim(), parts[2].trim(), parts[3].trim())
                    addressRepo.save(res)
                }

                Message(null,res,null,date,
                    message.subject,message.body,ctype)
            }

        }

        messageRepo.save(mEntity)


    }

    override fun getMessages(
        pageNumber: Int?,
        limit: Int?,
        date: LocalDate?,
        subject: String?,
        body: String?,
        channel: String?,
        state: String?,
        priority: Int?,
        email: String?,
        city: String?,
        country: String?,
        street: String?,
        telephone: String?
    ): List<MessageDTO> {
        val pageable = if(pageNumber != null && limit != null) {
            PageRequest.of(pageNumber-1, limit)
        } else {
            null
        }

        val finalChannel = if(channel != null) {
            when (channel) {
                "phone call" -> ChannelType.PHONE_CALL
                "text message" -> ChannelType.TEXT_MESSAGE
                "email" -> ChannelType.EMAIL
                "address" -> ChannelType.ADDRESS
                else -> ChannelType.OTHER
            }
        }
        else{
            null
        }

        val finalState = if(state != null) {
            when(state){
            "received" -> MachineStateType.RECEIVED
            "read" -> MachineStateType.READ
            "discarded" -> MachineStateType.DISCARDED
            "processing" -> MachineStateType.PROCESSING
            "done" -> MachineStateType.DONE
            "failed" -> MachineStateType.FAILED
            else -> throw InvalidStateException("This state is an invalid one.")
            }
        } else {
            null
        }


        val finalPriority = if(priority != null){
            when(priority){
                1 -> PriorityValue.LOW
                2 -> PriorityValue.MEDIUM
                3 -> PriorityValue.HIGH
                else -> throw InvalidPriorityException("This priority value is an invalid one.")
            }
        } else {
            null
        }

        val messages = messageRepo.findAllFiltered(date, subject, body, finalChannel, finalState, finalPriority, email, city, country, street, telephone, pageable)
        return messages.map { m ->
            val contacts = when(m.channel){
                ChannelType.PHONE_CALL, ChannelType.TEXT_MESSAGE -> m.telephoneSender?.contacts
                ChannelType.EMAIL -> m.emailSender?.contacts
                else -> {
                    m.addressSender?.contacts
                }
            }
            val res = m.toDTO()
            if (contacts != null) {
                if (contacts.isEmpty()){
                    val userUnknown = GeneralContactDTO(null, "???", "???", "unknown", null, null)
                    res.relatedContacts.add(userUnknown)
                }
                else {
                    res.relatedContacts.addAll(contacts.map{it.toDTO()})
                }
            }
            res
        }
    }

    override fun getMessageById(id: Long): MessageDTO? {
        val message = messageRepo.findById(id).orElse(null) ?: throw MessageNotFoundException("Message with id : $id not found")
        val contacts = when(message.channel){
            ChannelType.PHONE_CALL, ChannelType.TEXT_MESSAGE -> message.telephoneSender?.contacts
            ChannelType.EMAIL -> message.emailSender?.contacts
            else -> {
                message.addressSender?.contacts
            }
        }
        val m = message.toDTO()
        if (contacts != null) {
            if (contacts.isEmpty()){
                val userUnknown = GeneralContactDTO(null, "???", "???", "unknown", null, null)
                m.relatedContacts.add(userUnknown)
            }
            else {
                m.relatedContacts.addAll(contacts.map{it.toDTO()})
            }
        }
        return m
    }

    override fun changeState(id: Long, dto: StateWithMessageDTO) : MessageDTO? {
        val message = messageRepo.findById(id).orElse(null)
        if(message==null){
            throw MessageNotFoundException("Message with id : $id not found")
        }
        else{
            when(message.state){
                MachineStateType.RECEIVED -> {
                    if(dto.state == "read")message.state = MachineStateType.READ
                    else{
                        throw InvalidStateException("Error with the input state, it should be 'read' ")
                    }
                }
                MachineStateType.READ -> {
                    when (dto.state) {
                        "processing" -> message.state = MachineStateType.PROCESSING
                        "discarded" -> message.state = MachineStateType.DISCARDED
                        "done" -> message.state = MachineStateType.DONE
                        "failed" -> message.state = MachineStateType.FAILED
                        else -> {
                            throw InvalidStateException("Error with the input state, it should be 'processing', 'discarded', 'done' or 'failed' ")
                        }
                    }
                }
                MachineStateType.PROCESSING -> {
                    if(dto.state == "done")message.state = MachineStateType.DONE
                    else{
                        throw InvalidStateException("Error with the input state, it should be 'done' ")
                    }
                }
                else -> {
                    throw InvalidStateException("Error with the input state, the message can't change its state anymore ")
                }

            }

            val action = ActionOnMessage(null,message.state, LocalDateTime.now(), message, dto.comment)
            message.addAction(action)
            messageRepo.save(message)
            actionOnMessageRepo.save(action)
            return message.toDTO()
        }
    }

    override fun getHistory(id: Long): List<ActionOnMessageDTO>{
        val message = messageRepo.findById(id).orElse(null)
        if(message==null){
            throw MessageNotFoundException("Message with id : $id not found")
        }
        else{
           return message.actions.map{it.toDTO()}
        }
    }

    override fun updatePriority(id: Long, newValue: Int) {
        val message = messageRepo.findById(id).orElse(null)
        if(message==null){
            throw MessageNotFoundException("Message with id : $id not found")
        }
        else{
            message.priority = when(newValue){
                1 -> PriorityValue.LOW
                2 -> PriorityValue.MEDIUM
                3 -> PriorityValue.HIGH
                else ->{
                    throw InvalidPriorityException(" '$newValue' is not a allowed value, please insert 1,2 or 3")
                }
            }

            messageRepo.save(message)
        }
    }


}