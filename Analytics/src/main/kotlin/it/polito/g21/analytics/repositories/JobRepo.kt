package it.polito.g21.analytics.repositories

import it.polito.g21.analytics.entities.JobOffer
import it.polito.g21.analytics.models.CountDTO
import it.polito.g21.analytics.models.TimeDTO
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface JobRepo : JpaRepository<JobOffer, Long> {

    @Query("SELECT new it.polito.g21.analytics.models.CountDTO(j.day, COUNT(j)) " +
            "FROM JobOffer j " +
            "WHERE j.month = :month AND j.year = :year AND j.status = :status " +
            "GROUP BY j.day " +
            "ORDER BY j.day")
    fun countJobsMonthly(status: String, month: Int, year: Int): List<CountDTO>

    @Query("SELECT new it.polito.g21.analytics.models.CountDTO(j.month, COUNT(j)) " +
            "FROM JobOffer j " +
            "WHERE j.year = :year AND j.status = :status " +
            "GROUP BY j.month " +
            "ORDER BY j.month")
    fun countJobsYearly(status: String, year: Int): List<CountDTO>

    @Query("""
    SELECT new it.polito.g21.analytics.models.TimeDTO(
        j2.month, 
        AVG((j2.year - j1.year) * 365 + (j2.month - j1.month) * 30 + (j2.day - j1.day))
    )
    FROM JobOffer j1 
    JOIN JobOffer j2 ON j1.jobId = j2.jobId
    WHERE j2.year = :year 
      AND j1.status = 'created' 
      AND j2.status = 'consolidated'
    GROUP BY j2.month
    ORDER BY j2.month
""")
    fun computeTimeYearly(year: Int): List<TimeDTO>

}