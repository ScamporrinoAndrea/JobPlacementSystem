package it.polito.g21.crm.entities

import jakarta.persistence.*

@Entity
class Email(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id : Long? = null,
    var mail : String,
) {
    @ManyToMany(mappedBy = "emails")
    val contacts : MutableSet<Contact> = mutableSetOf()

    fun addContact(c : Contact){
        contacts.add(c)
        c.emails.add(this)
    }

    @OneToMany(mappedBy = "emailSender")
    val messages = mutableSetOf<Message>()

    fun addMessage(m : Message){
        m.emailSender = this
        messages.add(m)
    }

}