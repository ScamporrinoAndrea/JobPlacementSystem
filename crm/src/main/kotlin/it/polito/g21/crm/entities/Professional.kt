package it.polito.g21.crm.entities

import it.polito.g21.crm.utils.CategoryType
import it.polito.g21.crm.utils.EmploymentState
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne

@Entity
class Professional(
    var skills: String,
    var employmentState: EmploymentState,
    var location: String,
    var dailyRate: Double,
    @OneToOne
    var contact : Contact
) : EntityBase<Long>()  {

    //One-to-many with JobOffer
    @OneToMany(mappedBy = "professional")
    var jobs : MutableList<JobOffer> = mutableListOf()

    fun addJob(job: JobOffer) {
        jobs.add(job)
        job.professional = this
    }

    fun removeJob(job: JobOffer) {
        jobs.remove(job)
        job.professional = null
    }

    //One-to-many with Notes
    @OneToMany(mappedBy = "professional")
    val notes = mutableSetOf<Note>()

    fun addNote(n: Note){
        n.professional = this;
        notes.add(n)
    }

}