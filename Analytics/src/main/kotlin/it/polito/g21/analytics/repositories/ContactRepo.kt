package it.polito.g21.analytics.repositories

import it.polito.g21.analytics.entities.Contact
import it.polito.g21.analytics.models.CountDTO
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ContactRepo : JpaRepository<Contact, Long> {

    @Query("SELECT new it.polito.g21.analytics.models.CountDTO(c.day, COUNT(c)) " +
            "FROM Contact c " +
            "WHERE c.month = :month AND c.year = :year AND c.category = :cat " +
            "GROUP BY c.day " +
            "ORDER BY c.day")
    fun countContactsMonthly(cat: String, month: Int, year: Int): List<CountDTO>

    @Query("SELECT new it.polito.g21.analytics.models.CountDTO(c.month, COUNT(c)) " +
            "FROM Contact c " +
            "WHERE c.year = :year AND c.category = :cat " +
            "GROUP BY c.month " +
            "ORDER BY c.month")
    fun countContactsYearly(cat: String, year: Int): List<CountDTO>
}