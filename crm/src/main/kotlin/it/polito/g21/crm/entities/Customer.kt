package it.polito.g21.crm.entities

import it.polito.g21.crm.utils.CategoryType
import it.polito.g21.crm.utils.JobStatus
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import org.springframework.boot.autoconfigure.batch.BatchProperties.Job

@Entity
class Customer(
    @OneToOne
    var contact : Contact
) : EntityBase<Long>() {

    //One-to-many with Joboffers
    @OneToMany(mappedBy = "customer")
    val jobs = mutableSetOf<JobOffer>()

    fun addJob(j: JobOffer){
        j.customer = this;
        jobs.add(j)
    }

    fun abortJobs(){
        for (j in jobs){
            j.customer = null
            j.status = JobStatus.ABORTED
        }
    }

    //One-to-many with Notes
    @OneToMany(mappedBy = "customer")
    val notes = mutableSetOf<Note>()

    fun addNote(n: Note){
        n.customer = this;
        notes.add(n)
    }


}