package br.uff.graduatesapi.repository

import br.uff.graduatesapi.model.Advisor
import br.uff.graduatesapi.model.Graduate
import org.springframework.data.jpa.repository.JpaRepository

interface GraduateRepository : JpaRepository<Graduate, Int> {

    fun findAllByWorkHistoryInstitutionId(institutionId: Int) : List<Graduate>
    fun findAllByCoursesAdvisorIsOrderByHistoryStatusDesc(
        advisor: Advisor
    ): MutableList<Graduate>?
}