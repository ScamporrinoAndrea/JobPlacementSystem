package it.polito.g21.crm.repositories

import it.polito.g21.crm.entities.Contact
import it.polito.g21.crm.entities.Message
import it.polito.g21.crm.utils.CategoryType
import it.polito.g21.crm.utils.ChannelType
import it.polito.g21.crm.utils.MachineStateType
import it.polito.g21.crm.utils.PriorityValue
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface MessageRepo : JpaRepository<Message, Long>{
    @Query(
        "SELECT m FROM Message m " +
                "LEFT JOIN m.emailSender e " +
                "LEFT JOIN m.addressSender a " +
                "LEFT JOIN m.telephoneSender t " +
                "WHERE (:date IS NULL OR m.date = :date)" +
                "AND (:subject IS NULL OR m.subject = :subject)" +
                "AND (:body IS NULL OR m.body LIKE %:body%)" +
                "AND (:channel IS NULL OR m.channel = :channel)" +
                "AND (:state IS NULL OR m.state = :state)" +
                "AND (:priority IS NULL OR m.priority = :priority)" +
                "AND (:email IS NULL OR e.mail = :email)" +
                "AND (:city IS NULL OR a.city = :city)" +
                "AND (:country IS NULL OR a.country = :country)" +
                "AND (:street IS NULL OR a.streetName = :street)" +
                "AND (:tel IS NULL OR t.number = :tel)"
    )
    fun findAllFiltered(date: LocalDate?, subject: String?, body: String?, channel: ChannelType?, state: MachineStateType?, priority: PriorityValue?,
                        email: String?, city:String?, country: String?, street: String?, tel: String?, pageable: Pageable?): List<Message>
}