package br.uff.graduatesapi.repository

import br.uff.graduatesapi.model.CNPQScholarship
import br.uff.graduatesapi.model.Graduate
import br.uff.graduatesapi.model.WorkHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface CNPQScholarshipRepository : JpaRepository<CNPQScholarship, Int> {

    @Modifying
    @Query("select scholarship from CNPQScholarship scholarship where scholarship.graduate = ?1 and scholarship.endYear IS NULL")
    fun findActualCNPQScholarshipByGraduate(graduate: Graduate)
}