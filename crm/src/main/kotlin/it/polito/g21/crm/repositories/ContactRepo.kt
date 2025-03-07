package it.polito.g21.crm.repositories

import it.polito.g21.crm.entities.Contact
import it.polito.g21.crm.utils.CategoryType
import it.polito.g21.crm.utils.EmploymentState
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.data.domain.Pageable

@Repository
interface ContactRepo : JpaRepository<Contact, Long> {
    @Query(
        "SELECT c FROM Contact c " +
                "LEFT JOIN c.emails e " +
                "LEFT JOIN c.addresses a " +
                "LEFT JOIN c.telephones t " +
                "LEFT JOIN c.professional p " +
                "WHERE (:name IS NULL OR c.name = :name) " +
                "AND (:surname IS NULL OR c.surname = :surname) " +
                "AND (:category IS NULL OR c.category = :category) " +
                "AND (:ssnCode IS NULL OR c.SSNCode = :ssnCode) " +
                "AND (:email IS NULL OR e.mail = :email)" +
                "AND (:city IS NULL OR a.city = :city)" +
                "AND (:country IS NULL OR a.country = :country)" +
                "AND (:street IS NULL OR a.streetName = :street)" +
                "AND (:tel IS NULL OR t.number = :tel)" +
                "AND (:skills IS NULL OR p.skills like %:skills%)" +
                "AND (:empState IS NULL OR p.employmentState = :empState)" +
                "AND (:location IS NULL OR p.location = :location)" +
                "AND (:dailyRate IS NULL OR p.dailyRate = :dailyRate)"
    )
    fun findAllFiltered(name: String?, surname: String?, category: CategoryType?, ssnCode: String?,
                        email: String?, city: String?, country: String?, street: String?,
                        tel: String?, skills: String?, empState: EmploymentState?, location: String?,
                        dailyRate: Double?, pageable: Pageable?): List<Contact>
}