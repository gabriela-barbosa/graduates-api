package br.uff.graduatesapi.repository

import br.uff.graduatesapi.model.Advisor
import br.uff.graduatesapi.model.Graduate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface GraduateRepository : JpaRepository<Graduate, Int> {

  fun findAllByOrderByUserNameAsc(): List<Graduate>
  fun findAllByWorkHistoryInstitutionId(institutionId: Int): List<Graduate>
  fun findAllByCoursesAdvisorIsOrderByHistoryStatusDesc(
    advisor: Advisor, pageable: Pageable
  ): Page<Graduate>
}