package it.polito.g21.crm.entities

import it.polito.g21.crm.utils.CategoryType
import jakarta.persistence.*



@Entity
open class Contact  (
    var name : String,
    var surname : String,
    var category: CategoryType,
    var SSNCode : String?,
): EntityBase<Long>() {
    @ManyToMany
    @JoinTable(name ="contact_email",
        joinColumns = [JoinColumn(name = "contact_id")],
        inverseJoinColumns = [JoinColumn(name = "email_id")]
    )
    val emails : MutableSet<Email> = mutableSetOf()

    fun addEmail(e : Email) {
        emails.add(e)
        e.contacts.add(this)
    }

    fun deleteEmail(e : Email){
        emails.remove(e)
        e.contacts.remove(this)
    }




    @ManyToMany
    @JoinTable(name ="contact_telephone",
        joinColumns = [JoinColumn(name = "contact_id")],
        inverseJoinColumns = [JoinColumn(name = "telephone_id")]
    )
    val telephones : MutableSet<Telephone> = mutableSetOf()



    fun addTelephone(t : Telephone) {
        telephones.add(t)
        t.contacts.add(this)
    }

    fun deleteTelephone(t : Telephone){
        telephones.remove(t)
        t.contacts.remove(this)
    }

    @ManyToMany
    @JoinTable(name ="contact_address",
        joinColumns = [JoinColumn(name = "contact_id")],
        inverseJoinColumns = [JoinColumn(name = "address_id")]
    )
    val addresses : MutableSet<Address> = mutableSetOf()

    fun addAddress(a : Address) {
        addresses.add(a)
        a.contacts.add(this)
    }

    fun deleteAddress( a : Address){
        addresses.remove(a)
        a.contacts.remove(this)
    }

    @OneToOne(mappedBy = "contact", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var customer : Customer?= null

    @OneToOne(mappedBy = "contact", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var professional : Professional?= null

}