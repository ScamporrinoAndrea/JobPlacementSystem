package it.polito.g21.crm.entities

import jakarta.persistence.*

@Entity
class Address(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id : Long? = null,
    var streetNumber: String,
    var streetName: String,
    var city: String,
    var country: String
) {
    @ManyToMany(mappedBy = "addresses")
    val contacts : MutableSet<Contact> = mutableSetOf()

    fun addContact(c : Contact){
        contacts.add(c)
        c.addresses.add(this)
    }

    @OneToMany(mappedBy = "addressSender")
    val messages = mutableSetOf<Message>()

    fun addMessage(m : Message){
        m.addressSender = this
        messages.add(m)
    }

    override fun toString(): String {
        return "$streetName, $streetNumber, $city, $country"
    }
}