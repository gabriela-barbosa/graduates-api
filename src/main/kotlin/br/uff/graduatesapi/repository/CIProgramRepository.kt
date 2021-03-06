package br.uff.graduatesapi.repository

import br.uff.graduatesapi.model.CIProgram
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface CIProgramRepository : JpaRepository<CIProgram, Int> {
    @Modifying
    @Query("update CIProgram program set program.active=false where program.id = ?1")
    override fun deleteById(id: Int)

    @Modifying
    @Query("select program from CIProgram program where program.active=true")
    fun findAllActives(): List<CIProgram>
}