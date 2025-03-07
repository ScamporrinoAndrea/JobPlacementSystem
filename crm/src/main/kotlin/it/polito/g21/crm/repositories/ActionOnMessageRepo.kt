package it.polito.g21.crm.repositories

import it.polito.g21.crm.entities.ActionOnMessage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ActionOnMessageRepo : JpaRepository<ActionOnMessage,Long>