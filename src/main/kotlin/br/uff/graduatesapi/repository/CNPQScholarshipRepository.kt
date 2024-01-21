package br.uff.graduatesapi.repository

import br.uff.graduatesapi.model.CNPQScholarship
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface CNPQScholarshipRepository : JpaRepository<CNPQScholarship, UUID> {

    @Query("select scholarship from CNPQScholarship scholarship where scholarship.graduate.id = ?1 and scholarship.endedAt IS NULL")
    fun findActualCNPQScholarshipByGraduateId(graduateId: UUID) : List<CNPQScholarship>
}