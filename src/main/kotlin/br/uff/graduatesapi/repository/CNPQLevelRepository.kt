package br.uff.graduatesapi.repository

import br.uff.graduatesapi.model.CIProgram
import br.uff.graduatesapi.model.CNPQLevel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface CNPQLevelRepository : JpaRepository<CNPQLevel, Int> {
    @Modifying
    @Query("update CNPQLevel level set level.active=false where level.id = ?1")
    override fun deleteById (id: Int)

    @Modifying
    @Query("select level from CNPQLevel level where level.active=true")
    fun findAllActives(): List<CNPQLevel>
}