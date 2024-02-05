package br.uff.graduatesapi.repository

import br.uff.graduatesapi.model.CNPQLevel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface CNPQLevelRepository : JpaRepository<CNPQLevel, UUID> {
    @Modifying
    @Query("update CNPQLevel level set level.active=false where level.id = ?1")
    override fun deleteById(id: UUID)

    @Modifying
    @Query("select level from CNPQLevel level where level.active=true")
    fun findAllActives(): List<CNPQLevel>

    @Modifying
    @Transactional
    @Query("update CNPQLevel level set level.name = ?1 where level.id = ?2")
    fun updateName(name: String, id: UUID)

    fun findCNPQLevelByNameIgnoreCase(name: String): CNPQLevel?
}