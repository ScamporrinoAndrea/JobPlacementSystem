package it.polito.g21.crm.entities

import jakarta.persistence.*


@Entity
class Telephone(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id : Long? = null,
    var number : String
) {
    @ManyToMany(mappedBy = "telephones")
    val contacts : MutableSet<Contact> = mutableSetOf()

    fun addContact(c : Contact){
        contacts.add(c)
        c.telephones.add(this)
    }

    @OneToMany(mappedBy = "telephoneSender")
    val messages = mutableSetOf<Message>()

    fun addMessage(m : Message){
        m.telephoneSender = this
        messages.add(m)
    }
}