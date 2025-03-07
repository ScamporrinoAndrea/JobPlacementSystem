package it.polito.g21.crm.repositories

import it.polito.g21.crm.entities.Address
import it.polito.g21.crm.entities.Note
import it.polito.g21.crm.utils.CategoryType
import org.aspectj.weaver.ast.Not
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface NoteRepo: JpaRepository<Note, Long> {

    @Query(
    "SELECT n FROM Note n " +
    "LEFT JOIN n.customer.contact cu " +
    "LEFT JOIN n.professional.contact p " +
    "WHERE (cu.id = :id AND cu.category = :categoryType) OR " +
    "(p.id = :id AND p.category = :categoryType)"
    )
    fun getNotesByContactId(id: Long, categoryType: CategoryType) : List<Note>
}