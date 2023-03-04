package br.uff.graduatesapi.repository

import br.uff.graduatesapi.model.CNPQScholarship
import br.uff.graduatesapi.model.Graduate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface CNPQScholarshipRepository : JpaRepository<CNPQScholarship, UUID> {

    @Query("select scholarship from CNPQScholarship scholarship where scholarship.graduate = ?1 and scholarship.endYear IS NULL")
    fun findActualCNPQScholarshipByGraduate(graduate: Graduate) : CNPQScholarship?
}