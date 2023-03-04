package br.uff.graduatesapi.repository

import br.uff.graduatesapi.model.Advisor
import br.uff.graduatesapi.model.Graduate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository
import java.util.*

interface GraduateRepository : PagingAndSortingRepository<Graduate, UUID> {

  fun findAllByOrderByCurrentWorkHistoryStatusDesc(pageable: Pageable = Pageable.unpaged()): Page<Graduate>

  fun findAllByWorkHistoriesInstitutionId(institutionId: UUID): List<Graduate>
  fun findAllByCoursesAdvisorIsOrderByHistoryStatusDesc(
    advisor: Advisor, pageable: Pageable = Pageable.unpaged()
  ): Page<Graduate>
}