package br.uff.graduatesapi.service

import br.uff.graduatesapi.model.Graduate
import br.uff.graduatesapi.model.WorkHistory
import br.uff.graduatesapi.repository.WorkHistoryRepository
import org.springframework.stereotype.Service

@Service
class WorkHistoryService(
    private val workHistoryRepository: WorkHistoryRepository,
    ) {
    fun findAllByGraduates(graduates: List<Graduate>): List<WorkHistory>? {
        return workHistoryRepository.findTopByGraduateOrderByCreatedAtDesc(graduates)
    }
    fun save(workHistory: WorkHistory): WorkHistory? {
        return workHistoryRepository.save(workHistory)
}
}