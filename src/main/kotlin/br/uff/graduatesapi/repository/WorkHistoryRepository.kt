package br.uff.graduatesapi.repository

import br.uff.graduatesapi.model.Graduate
import br.uff.graduatesapi.model.WorkHistory
import org.springframework.data.jpa.repository.JpaRepository

interface WorkHistoryRepository : JpaRepository<WorkHistory, Int> {
    fun findAllByGraduateIn(graduates: List<Graduate>): List<WorkHistory>?
}