package br.uff.graduatesapi.repository

import br.uff.graduatesapi.model.Advisor
import br.uff.graduatesapi.model.Graduate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.PagingAndSortingRepository

interface GraduateRepository : PagingAndSortingRepository<Graduate, Int> {

  fun findAllByOrderByCurrentWorkHistoryStatusDesc(pageable: Pageable = Pageable.unpaged()): Page<Graduate>

  fun findAllByWorkHistoriesInstitutionId(institutionId: Int): List<Graduate>
}